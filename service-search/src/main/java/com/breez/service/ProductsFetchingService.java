package com.breez.service;

import com.breez.dto.ProductDto;
import com.breez.exception.NoProductsFoundException;
import com.breez.exception.ServerException;
import com.breez.model.ProductChunkResult;
import com.breez.service.marketplace.MarketplaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductsFetchingService {

	private static final Logger logger = LoggerFactory.getLogger(ProductsFetchingService.class);

	private final RedisService redisService;

	private static final String REDIS_PRODUCTS_PREFIX = "products:%s:%s:%s";
	private static final String REDIS_NEXT_PAGE_SUFFIX = ":nextPage";
	private static final int CHUNK_SIZE = 10;

	@Autowired
	public ProductsFetchingService(RedisService redisService) {
		this.redisService = redisService;
	}

	public ProductChunkResult getProductChunk(MarketplaceService marketplaceService, String sessionId, String title, String sort, int chunkIndex) {
		String marketplaceId = marketplaceService.getMarketplaceIdentifier();
		String searchParamsString = String.format("title=%s&sort=%s", title, sort);
		String searchHash = DigestUtils.md5DigestAsHex(searchParamsString.getBytes(StandardCharsets.UTF_8));

		String productsKey = String.format(REDIS_PRODUCTS_PREFIX, marketplaceId, sessionId, searchHash);
		String nextPageKey = productsKey + REDIS_NEXT_PAGE_SUFFIX;

		long startIndex = (long) chunkIndex * CHUNK_SIZE;
		long endIndex = startIndex + CHUNK_SIZE - 1;
		List<Object> finalProductsChunk = Collections.emptyList();
		try {
			while (true) {
				Long currentCacheSize = redisService.getListSize(productsKey);
				currentCacheSize = (currentCacheSize == null) ? 0L : currentCacheSize;
				Integer nextPageMarker = redisService.getIntValue(nextPageKey);
				boolean isSourceExhausted = (nextPageMarker != null && nextPageMarker == -1);
				logger.debug("Session [{}], Market [{}], Search [{}], Chunk [{}]: Loop check. Cache size: {}, Need index: {}, Source exhausted: {}",
						sessionId, marketplaceId, searchHash, chunkIndex, currentCacheSize, endIndex, isSourceExhausted);
				if (currentCacheSize > endIndex || isSourceExhausted) {
					break;
				}
				int pageToFetch;
				pageToFetch = Objects.requireNonNullElse(nextPageMarker, 1);
				Map<String, String> parameters = marketplaceService.getSearchParameters(title, sort, String.valueOf(pageToFetch));
				List<ProductDto> productsFromSource = marketplaceService.fetchProducts(parameters);
				if (productsFromSource == null || productsFromSource.isEmpty()) {
					logger.info("Session [{}], Market [{}], Search [{}], Chunk [{}]: Source ({}) returned no products for page {}. Marking as exhausted.",
							sessionId, marketplaceId, searchHash, chunkIndex, marketplaceId, pageToFetch);
					redisService.saveValue(nextPageKey, -1);
					redisService.setExpire(productsKey);
				} else {
					List<Object> productsToCache = productsFromSource.stream().map(obj -> (Object) obj).toList();
					redisService.pushToList(productsKey, productsToCache);
					int newNextPage = pageToFetch + 1;
					redisService.saveValue(nextPageKey, newNextPage);
					redisService.setExpire(productsKey);
				}
			}
			Long finalCacheSizeCheck = redisService.getListSize(productsKey);
			finalCacheSizeCheck = (finalCacheSizeCheck == null) ? 0L : finalCacheSizeCheck;
			if (finalCacheSizeCheck > startIndex) {
				finalProductsChunk = redisService.getListRange(productsKey, startIndex, endIndex);
			}
			if (finalProductsChunk == null) {
				finalProductsChunk = Collections.emptyList();
			}

			List<ProductDto> resultProducts = finalProductsChunk.stream()
					.filter(Objects::nonNull)
					.map(obj -> (ProductDto) obj)
					.collect(Collectors.toList());

			Long finalCacheSize = redisService.getListSize(productsKey);
			finalCacheSize = (finalCacheSize == null) ? 0L : finalCacheSize;
			Integer finalNextPage = redisService.getIntValue(nextPageKey);
			boolean isFinalSourceExhausted = (finalNextPage != null && finalNextPage == -1);
			boolean hasMore = !isFinalSourceExhausted || (finalCacheSize > endIndex + 1);
			logger.info("Session [{}], Market [{}], Search [{}], Chunk [{}]: Final result: {} products, hasMore: {}. Final Cache Size: {}, Final Next Page: {}", sessionId, marketplaceId, searchHash, chunkIndex, resultProducts.size(), hasMore, finalCacheSize, finalNextPage);
			if (resultProducts.isEmpty()) {
				throw new NoProductsFoundException(marketplaceService.getMarketplaceIdentifier() + ": no products found");
			}
			return new ProductChunkResult(resultProducts, hasMore);
		} catch (IOException e) {
			throw new ServerException(marketplaceId + ": Failed to fetch data from source");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ServerException(marketplaceId + ": Interrupted while processing request");
		}
	}

}
