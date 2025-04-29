package com.breez.service;

import com.breez.exception.ServerException;
import com.breez.model.MarketplaceState;
import com.breez.model.ProductChunkResult;
import com.breez.service.marketplace.MarketplaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CombinedProductFetchingService {

	private static final Logger logger = LoggerFactory.getLogger(CombinedProductFetchingService.class);

	private final RedisService redisService;

	private static final String REDIS_PRODUCTS_PREFIX_TPL = "products:%s:%s:%s";
	private static final String REDIS_NEXT_PAGE_SUFFIX = ":nextPage";
	private static final int COMBINED_CHUNK_SIZE = 10;

	@Autowired
	public CombinedProductFetchingService(RedisService redisService) {
		this.redisService = redisService;
	}

	public ProductChunkResult getCombinedProductChunk(String sessionId, String title, String sort, int chunkIndex, List<MarketplaceService> selectedMarketplaceServices) {
		List<String> selectedIds = selectedMarketplaceServices.stream()
				.map(MarketplaceService::getMarketplaceIdentifier)
				.toList();

		String searchParamsString = String.format("title=%s&sort=%s", title, sort);
		String searchHash = DigestUtils.md5DigestAsHex(searchParamsString.getBytes(StandardCharsets.UTF_8));

		int numberOfMarketplaces = selectedMarketplaceServices.size();
		long globalStartIndex = (long) chunkIndex * COMBINED_CHUNK_SIZE;
		long globalEndIndex = globalStartIndex + COMBINED_CHUNK_SIZE - 1;

		Map<String, MarketplaceState> states = new ConcurrentHashMap<>();
		for (MarketplaceService client : selectedMarketplaceServices) {
			states.put(client.getMarketplaceIdentifier(), new MarketplaceState(client));
		}

		List<Map<String, Object>> combinedProductsResult = new ArrayList<>(COMBINED_CHUNK_SIZE);
		boolean needMoreFetching = true;

		try {
			while (combinedProductsResult.size() < COMBINED_CHUNK_SIZE && needMoreFetching) {
				needMoreFetching = false;
				boolean fetchedThisCycle = false;
				fillResultFromCache(combinedProductsResult, globalStartIndex, globalEndIndex, states, searchHash, sessionId, selectedMarketplaceServices);
				if (combinedProductsResult.size() < COMBINED_CHUNK_SIZE) {
					updateAllMarketplaceStates(states, sessionId, searchHash);
					MarketplaceState sourceToFetch = chooseSourceToFetch(states, selectedMarketplaceServices);
					if (sourceToFetch != null) {
						needMoreFetching = true;
						int pageToFetch = sourceToFetch.getNextPageToFetch();
						boolean success = fetchAndCacheForSource(sourceToFetch, sessionId, title, sort, searchHash, pageToFetch);
						if (success) {
							fetchedThisCycle = true;
							updateMarketplaceState(sourceToFetch, sessionId, searchHash);
						} else {
							sourceToFetch.setExhausted(true);
							if (!sourceToFetch.isFetchFailed()) {
								String nextPageKey = getNextPageKey(sourceToFetch.getClient().getMarketplaceIdentifier(), sessionId, searchHash);
								redisService.saveValue(nextPageKey, -1);
								redisService.setExpire(getProductsKey(sourceToFetch.getClient().getMarketplaceIdentifier(), sessionId, searchHash));
							}
						}
					}
				}
				if (!fetchedThisCycle && combinedProductsResult.size() < COMBINED_CHUNK_SIZE) {
					boolean allExhausted = states.values().stream().allMatch(MarketplaceState::isExhausted);
					if (allExhausted) {
						needMoreFetching = false;
					}
				}

			}

			updateAllMarketplaceStates(states, sessionId, searchHash);
			long totalFinalCacheSize = states.values().stream().mapToLong(MarketplaceState::getCacheSize).sum();
			boolean anySourceNotExhausted = states.values().stream().anyMatch(s -> !s.isExhausted());

			boolean elementExistsAfterEnd = false;
			if (anySourceNotExhausted) {
				elementExistsAfterEnd = true;
			} else {
				long nextGlobalIndex = globalEndIndex + 1;
				if (!selectedMarketplaceServices.isEmpty()) {
					String nextSourceId = selectedMarketplaceServices.get((int) (nextGlobalIndex % numberOfMarketplaces)).getMarketplaceIdentifier();
					long nextLocalIndex = nextGlobalIndex / numberOfMarketplaces;
					MarketplaceState nextState = states.get(nextSourceId);
					if (nextState != null && nextState.getCacheSize() > nextLocalIndex) {
						elementExistsAfterEnd = true;
					}
				}
			}
			boolean hasMore = elementExistsAfterEnd;
			logger.info("Session [{}], Search [{}], Marketplaces {}, Chunk [{}]: Final combined result: {} products, hasMore: {}. Total cache size for selection: {}",
					sessionId, searchHash, selectedIds, chunkIndex, combinedProductsResult.size(), hasMore, totalFinalCacheSize);
			return new ProductChunkResult(combinedProductsResult, hasMore);
		} catch (Exception e) {
			throw new ServerException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error during product fetching");
		}
	}

	private void fillResultFromCache(List<Map<String, Object>> combinedProductsResult,
									 long globalStartIndex, long globalEndIndex,
									 Map<String, MarketplaceState> states,
									 String searchHash, String sessionId,
									 List<MarketplaceService> selectedMarketplaceServices) {
		combinedProductsResult.clear();
		int numberOfMarketplaces = selectedMarketplaceServices.size();
		if (numberOfMarketplaces == 0) {
			return;
		}

		for (long currentGlobalIndex = globalStartIndex; currentGlobalIndex <= globalEndIndex; currentGlobalIndex++) {
			int marketplaceIndex = (int) (currentGlobalIndex % numberOfMarketplaces);
			long localIndex = currentGlobalIndex / numberOfMarketplaces;
			MarketplaceService client = selectedMarketplaceServices.get(marketplaceIndex);
			String marketplaceId = client.getMarketplaceIdentifier();
			MarketplaceState state = states.get(marketplaceId);

			if (state == null) continue;
			String productsKey = getProductsKey(marketplaceId, sessionId, searchHash);
			if (state.getCacheSize() < 0) {
				state.setCacheSize(Optional.ofNullable(redisService.getListSize(productsKey)).orElse(0L));
			}

			if (state.getCacheSize() > localIndex) {
				List<Object> itemResult = redisService.getListRange(productsKey, localIndex, localIndex);
				if (itemResult != null && !itemResult.isEmpty() && itemResult.get(0) instanceof Map) {
					Map<String, Object> productMap = (Map<String, Object>) itemResult.get(0);
					combinedProductsResult.add(productMap);
				}
			}
		}
		logger.debug("Session [{}], Search [{}]: Filled {} items from cache for range {}-{}",
				sessionId, searchHash, combinedProductsResult.size(), globalStartIndex, globalEndIndex);
	}

	private MarketplaceState chooseSourceToFetch(Map<String, MarketplaceState> states, List<MarketplaceService> selectedMarketplaceServices) {
		MarketplaceState bestCandidate = null;
		long minNextGlobalIndex = Long.MAX_VALUE;
		int numberOfMarketplaces = selectedMarketplaceServices.size();
		if (numberOfMarketplaces == 0) {
			return null;
		}
		for (MarketplaceState state : states.values()) {
			if (!state.isExhausted() && state.getNextPageToFetch() != -1) {
				long estimatedNextLocalIndex = state.getCacheSize();
				int clientIndexInSelectedList = -1;
				MarketplaceService stateClient = state.getClient();
				for(int i = 0; i < numberOfMarketplaces; i++) {
					if (selectedMarketplaceServices.get(i).getMarketplaceIdentifier().equals(stateClient.getMarketplaceIdentifier())) {
						clientIndexInSelectedList = i;
						break;
					}
				}
				if (clientIndexInSelectedList == -1) {
					continue;
				}
				long estimatedNextGlobalIndex = estimatedNextLocalIndex * numberOfMarketplaces + clientIndexInSelectedList;

				if (estimatedNextGlobalIndex < minNextGlobalIndex) {
					minNextGlobalIndex = estimatedNextGlobalIndex;
					bestCandidate = state;
				}
			}
		}
		return bestCandidate;
	}

	private boolean fetchAndCacheForSource(MarketplaceState sourceState, String sessionId, String title, String sort, String searchHash, int pageToFetch) {
		MarketplaceService client = sourceState.getClient();
		String marketplaceId = client.getMarketplaceIdentifier();
		String productsKey = getProductsKey(marketplaceId, sessionId, searchHash);
		String nextPageKey = getNextPageKey(marketplaceId, sessionId, searchHash);
		sourceState.setFetchFailed(false);

		try {
			Map<String, String> parameters = client.getSearchParameters(title, sort, String.valueOf(pageToFetch));
			List<Map<String, Object>> productsFromSource = client.fetchProducts(parameters);

			if (productsFromSource == null || productsFromSource.isEmpty()) {
				logger.info("Session [{}], Search [{}]: Source ({}) returned no products for page {}.", sessionId, searchHash, marketplaceId, pageToFetch);
				redisService.saveValue(nextPageKey, -1);
				redisService.setExpire(productsKey);
				return false;
			} else {
				List<Object> productsToCache = productsFromSource.stream().map(obj -> (Object) obj).toList();
				redisService.pushToList(productsKey, productsToCache);
				int newNextPage = pageToFetch + 1;
				redisService.saveValue(nextPageKey, newNextPage);
				redisService.setExpire(productsKey);
				redisService.setExpire(nextPageKey);
				logger.info("Session [{}], Search [{}]: Successfully fetched and cached {} products from {} page {}. Next page: {}", sessionId, searchHash, productsToCache.size(), marketplaceId, pageToFetch, newNextPage);
				return true;
			}
		} catch (IOException | InterruptedException e) {
			if (e instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			sourceState.setFetchFailed(true);
			return false;
		}
	}

	private void updateAllMarketplaceStates(Map<String, MarketplaceState> states, String sessionId, String searchHash) {
		for (MarketplaceState state : states.values()) {
			updateMarketplaceState(state, sessionId, searchHash);
		}
	}

	private void updateMarketplaceState(MarketplaceState state, String sessionId, String searchHash) {
		String marketplaceId = state.getClient().getMarketplaceIdentifier();
		String productsKey = getProductsKey(marketplaceId, sessionId, searchHash);
		String nextPageKey = getNextPageKey(marketplaceId, sessionId, searchHash);
		state.setCacheSize(Optional.ofNullable(redisService.getListSize(productsKey)).orElse(0L));
		Integer nextPageMarker = redisService.getIntValue(nextPageKey);
		if (nextPageMarker == null) {
			state.setNextPageToFetch(1);
			state.setExhausted(false);
		} else if (nextPageMarker == -1) {
			state.setNextPageToFetch(-1);
			state.setExhausted(true);
		} else {
			state.setNextPageToFetch(nextPageMarker);
			state.setExhausted(false);
		}
	}

	private String getProductsKey(String marketplaceId, String sessionId, String searchHash) {
		return String.format(REDIS_PRODUCTS_PREFIX_TPL, marketplaceId, sessionId, searchHash);
	}

	private String getNextPageKey(String marketplaceId, String sessionId, String searchHash) {
		return getProductsKey(marketplaceId, sessionId, searchHash) + REDIS_NEXT_PAGE_SUFFIX;
	}

}