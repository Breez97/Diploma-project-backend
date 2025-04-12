package com.breez.service;

import com.breez.exception.ClientException;
import com.breez.exception.DataParsingException;
import com.breez.exception.ServerException;
import com.breez.util.ozon.OzonAllProductsUtil;
import com.breez.util.ozon.OzonSingleProductUtil;
import lombok.RequiredArgsConstructor;
import org.brotli.dec.BrotliInputStream;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.breez.constants.Constants.*;

@Service
@RequiredArgsConstructor
public class OzonService implements HttpService {

	private final HttpClient httpClient;
	private final OzonSingleProductUtil ozonSingleProductUtil;
	private final OzonAllProductsUtil ozonAllProductsUtil;

	@Override
	public List<Map<String, Object>> makeRequest(Map<String, String> parameters) throws IOException, InterruptedException {
		String url = OZON_BASE_URL + "/searchSuggestions/search/?text=" + parameters.get("title") + "&from_global=true";
		String responseBody = getResponseBody(url);
		String category = ozonAllProductsUtil.extractFirstCategoryValue(responseBody);
		String searchUrl = OZON_BASE_URL + category + "?category_was_predicted=" + (category != null)
				+ "&deny_category_prediction=true&from_global=true&layout_page_index=2&page=" + parameters.get("page")
				+ "&paginator_token=3618992&sorting="+ parameters.get("sort") + "&start_page_id=940e64c1968c1684bcf866c50f7c93b6&text=" + parameters.get("title");
		String searchResponseBody = getSearchResponseBody(searchUrl);
		return ozonAllProductsUtil.getAllProductsFromResponse(searchResponseBody);
	}

	@Override
	public Map<String, Object> makeRequestProduct(long id) throws IOException, InterruptedException {
		String url = OZON_BASE_URL + "/product/" + id;
		String responseBody = getResponseBody(url);
		return ozonSingleProductUtil.getSingleProductFromResponse(responseBody, id);
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
			response.headers().map().forEach((k, v) -> System.out.println("  " + k + ": " + v));
			byte[] responseBodyBytes = response.body();
			Optional<String> contentEncoding = response.headers().firstValue("Content-Encoding");
			if (contentEncoding.isPresent()) {
				String encoding = contentEncoding.get().toLowerCase();
				try {
					if (encoding.contains("br")) {
						return decompressBrotli(responseBodyBytes);
					}
				} catch (IOException e) {
					throw new DataParsingException(HttpStatus.INTERNAL_SERVER_ERROR, "Ozon: " + e.getMessage());
				}
			}
			return null;
		} else if (statusCode >= 400 && statusCode <= 499) {
			throw new ClientException(HttpStatus.valueOf(statusCode), "Ozon: Client error");
		} else if (statusCode >= 500 && statusCode <= 599) {
			throw new ServerException(HttpStatus.valueOf(statusCode), "Ozon: Server error");
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
