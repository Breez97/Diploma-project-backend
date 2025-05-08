package com.breez.service.marketplace;

import com.breez.dto.ProductDetailsDto;
import com.breez.dto.ProductDto;
import com.breez.exception.ClientException;
import com.breez.exception.DataParsingException;
import com.breez.exception.ServerException;
import com.breez.service.MarketplaceService;
import com.breez.util.marketplace.ozon.OzonAllProductsUtil;
import com.breez.util.marketplace.ozon.OzonSingleProductUtil;
import org.brotli.dec.BrotliInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.breez.constants.Constants.*;
import static com.breez.constants.Constants.COMMON_SORT_PRICE_DESC;
import static com.breez.constants.Constants.COMMON_SORT_RATING;
import static com.breez.constants.Constants.OZON_SORT_POPULAR;
import static com.breez.constants.Constants.OZON_SORT_PRICE_ASC;
import static com.breez.constants.Constants.OZON_SORT_PRICE_DESC;
import static com.breez.constants.Constants.OZON_SORT_RATING;

@Service
public class OzonService implements MarketplaceService {

	private final HttpClient httpClient;
	private final OzonSingleProductUtil ozonSingleProductUtil;
	private final OzonAllProductsUtil ozonAllProductsUtil;

	@Autowired
	public OzonService(HttpClient httpClient, OzonSingleProductUtil ozonSingleProductUtil, OzonAllProductsUtil ozonAllProductsUtil) {
		this.httpClient = httpClient;
		this.ozonSingleProductUtil = ozonSingleProductUtil;
		this.ozonAllProductsUtil = ozonAllProductsUtil;
	}

	@Override
	public Map<String, String> getSearchParameters(String title, String sort, String page) {
		Map<String, String> parameters = new HashMap<>();
		String resultTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
		String resultSort = switch (sort) {
			case COMMON_SORT_NEW -> OZON_SORT_NEW;
			case COMMON_SORT_PRICE_ASC -> OZON_SORT_PRICE_ASC;
			case COMMON_SORT_PRICE_DESC -> OZON_SORT_PRICE_DESC;
			case COMMON_SORT_RATING -> OZON_SORT_RATING;
			default -> OZON_SORT_POPULAR;
		};
		parameters.put("title", resultTitle);
		parameters.put("sort", resultSort);
		parameters.put("page", page);
		return parameters;
	}

	@Override
	public List<ProductDto> fetchProducts(Map<String, String> parameters) throws IOException, InterruptedException {
		String url = OZON_BASE_URL + "/searchSuggestions/search/?text=" + parameters.get("title") + "&from_global=true";
		String responseBody = getResponseBody(url);
		String category = ozonAllProductsUtil.extractFirstCategoryValue(responseBody);
		String searchUrl = OZON_BASE_URL + category + "?category_was_predicted=" + (category != null)
				+ "&deny_category_prediction=true&from_global=true&layout_page_index=2&page=" + parameters.get("page")
				+ "&paginator_token=3618992&sorting="+ parameters.get("sort") + "&start_page_id=940e64c1968c1684bcf866c50f7c93b6&text=" + parameters.get("title");
		String searchResponseBody = getSearchResponseBody(searchUrl);
		return ozonAllProductsUtil.getAllProductsFromResponse(searchResponseBody);
	}

	public ProductDetailsDto fetchSingleProduct(Long id) {
		String url = OZON_BASE_URL + "/product/" + id;
		try {
			String responseBody = getResponseBody(url);
			return ozonSingleProductUtil.getSingleProductFromResponse(responseBody, id);
		} catch (IOException e) {
			throw new ServerException("Ozon: Failed to fetch data from source");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ServerException("Ozon: Interrupted while processing request");
		}
	}

	@Override
	public String getMarketplaceIdentifier() {
		return OZON;
	}

	private String getResponseBody(String url) throws IOException, InterruptedException {
		HttpRequest request = createRequest(url)
				.GET()
				.build();
		HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
		return convertResponseBody(response);
	}

	private String getSearchResponseBody(String url) throws IOException, InterruptedException {
		HttpRequest request = createRequest(url)
				.GET()
				.build();
		HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
		return convertResponseBody(response);
	}

	private HttpRequest.Builder createRequest(String url) {
		return HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Cookie", OZON_COOKIE)
				.header("User-Agent", "insomnia/11.0.2")
				.header("Accept", "*/*")
				.header("Accept-Encoding", "gzip, deflate, br")
				.header("Accept-Language", "ru-RU,ru;q=0.9");
	}

	private String convertResponseBody(HttpResponse<byte[]> response) {
		int statusCode = response.statusCode();

		if (statusCode == 200) {
			byte[] responseBodyBytes = response.body();
			Optional<String> contentEncoding = response.headers().firstValue("Content-Encoding");
			if (contentEncoding.isPresent()) {
				String encoding = contentEncoding.get().toLowerCase();
				try {
					if (encoding.contains("br")) {
						return decompressBrotli(responseBodyBytes);
					}
				} catch (IOException e) {
					throw new DataParsingException("Ozon: " + e.getMessage());
				}
			}
			return null;
		} else if (statusCode >= 400 && statusCode <= 499) {
			throw new ClientException("Ozon: Client error");
		} else if (statusCode >= 500 && statusCode <= 599) {
			throw new ServerException("Ozon: Server error");
		}
		return null;
	}

	private String decompressBrotli(byte[] compressed) throws IOException {
		if (compressed == null || compressed.length == 0) {
			return "";
		}
		try (ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
			 BrotliInputStream bris = new BrotliInputStream(bis)) {
			return new String(bris.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

}
