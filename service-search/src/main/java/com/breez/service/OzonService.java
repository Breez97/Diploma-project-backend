package com.breez.service;

import com.breez.exception.ClientException;
import com.breez.exception.DataParsingException;
import com.breez.exception.ServerException;
import com.breez.util.OzonUtil;
import lombok.RequiredArgsConstructor;
import org.brotli.dec.BrotliInputStream;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.breez.constants.Constants.*;

@Service
@RequiredArgsConstructor
public class OzonService {

	private final HttpClient httpClient;
	private final OzonUtil ozonJsonParserUtil;

	public List<Map<String, Object>> makeRequest(String title) throws IOException, InterruptedException {
		String responseBody = getResponseBody(title);
		String category = ozonJsonParserUtil.extractFirstCategoryValue(responseBody);
		String searchResponseBody = getSearchResponseBody(title, category);
		return ozonJsonParserUtil.extractResultProducts(searchResponseBody);
	}

	private String getResponseBody(String title) throws IOException, InterruptedException {
		HttpRequest request = createRequest(OZON_BASE_URL + "/searchSuggestions/search/?text=" + title + "&from_global=true")
				.GET()
				.build();

		HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
		return convertResponseBody(response);
	}

	private String getSearchResponseBody(String title, String category) throws IOException, InterruptedException {
		HttpRequest request = createRequest(OZON_BASE_URL + category + "?category_was_predicted=" + (category != null) + "&deny_category_prediction=true&from_global=true&layout_page_index=2&page=1&paginator_token=3618992&search_page_state=ZTyWv3PbdnpxslZbkHhw7VH_jtjEr10CyAedWaImdMe__4v5IFS4TUkv7-w_R4DT4FJk1JIuc4-cWKHyUsFmg4wGpQ54FRjeGeNy2bPD-lbxsyn4b-6baUmQAiDy&sorting="+ OZON_SORT_POPULAR + "&start_page_id=940e64c1968c1684bcf866c50f7c93b6&text=" + title)
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
