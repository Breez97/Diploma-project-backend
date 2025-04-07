package com.breez.service;

import com.breez.component.WebClientFactory;
import com.breez.exception.ClientException;
import com.breez.exception.DataParsingException;
import com.breez.exception.ServerException;
import com.breez.util.WildberriesUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.breez.constants.Constants.*;

@Service
@RequiredArgsConstructor
public class WildberriesService implements MarketplaceService {

	private final WebClientFactory webClientFactory;
	private final WildberriesUtil wildberriesUtil;

	@Override
	public Mono<List<Map<String, Object>>> fetchData(String title) {
		return fetchData(title, createDefaultParams());
	}

	@Override
	public Mono<List<Map<String, Object>>> fetchData(String title, Map<String, Object> params) {
		int page = (int) params.getOrDefault("page", WILDBERRIES_PAGE);
		String sex = (String) params.getOrDefault("sex", WILDBERRIES_SEX_COMMON);
		String sort = (String) params.getOrDefault("sort", WILDBERRIES_SORT_POPULAR);
		int amount = (int) params.getOrDefault("amount", WILDBERRIES_AMOUNT);

		return webClientFactory.createWebClient(WILDBERRIES_BASE_URL)
				.get()
				.uri(uriBuilder -> uriBuilder
						.path("/exactmatch/ru/" + sex + "/v9/search")
						.queryParam("curr", "rub")
						.queryParam("dest", -1257786)
						.queryParam("lang", "ru")
						.queryParam("page", page)
						.queryParam("query", title)
						.queryParam("resultset", "catalog")
						.queryParam("sort", sort)
						.queryParam("spp", amount)
						.queryParam("suppressSpellcheck", true)
						.build())
				.header("user-agent", WILDBERRIES_USER_AGENT)
				.header("x-captcha-id", WILDBERRIES_X_CAPTCHA_ID)
				.retrieve()
				.onStatus(HttpStatusCode::is4xxClientError, response -> {
					Mono.error(new ClientException((HttpStatus) response.statusCode(), "Wildberries: client error"));
					return null;
				})
				.onStatus(HttpStatusCode::is5xxServerError, response -> {
					Mono.error(new ServerException((HttpStatus) response.statusCode(), "Wildberries: server error"));
					return null;
				})
				.bodyToFlux(DataBuffer.class)
				.map(this::convertBufferToString)
				.reduce((str1, str2) -> str1 + str2)
				.flatMap(wildberriesUtil::extractResultProducts)
				.onErrorMap(e -> new DataParsingException(HttpStatus.INTERNAL_SERVER_ERROR, "Wildberries: " + e.getMessage()));
	}

	@Override
	public Map<String, Object> createDefaultParams() {
		Map<String, Object> params = new HashMap<>();
		params.put("page", WILDBERRIES_PAGE);
		params.put("sex", WILDBERRIES_SEX_COMMON);
		params.put("sort", WILDBERRIES_SORT_POPULAR);
		params.put("amount", WILDBERRIES_AMOUNT);
		return params;
	}

	private String convertBufferToString(DataBuffer buffer) {
		byte[] bytes = new byte[buffer.readableByteCount()];
		buffer.read(bytes);
		return new String(bytes, StandardCharsets.UTF_8);
	}

	public Mono<List<Map<String, Object>>> fetchDataProduct(long id) {
		return webClientFactory.createWebClient(wildberriesUtil.getProductInfoLink(id))
				.get()
				.header("user-agent", WILDBERRIES_USER_AGENT)
				.header("x-captcha-id", WILDBERRIES_X_CAPTCHA_ID)
				.retrieve()
				.onStatus(HttpStatusCode::is4xxClientError, response -> {
					Mono.error(new ClientException((HttpStatus) response.statusCode(), "Wildberries: client error"));
					return null;
				})
				.onStatus(HttpStatusCode::is5xxServerError, response -> {
					Mono.error(new ServerException((HttpStatus) response.statusCode(), "Wildberries: server error"));
					return null;
				})
				.bodyToFlux(DataBuffer.class)
				.map(this::convertBufferToString)
				.reduce((str1, str2) -> str1 + str2)
				.flatMap(this::processResponseProduct)
				.onErrorMap(e -> new DataParsingException(HttpStatus.INTERNAL_SERVER_ERROR, "Wildberries: " + e.getMessage()));
	}

	private Mono<List<Map<String, Object>>> processResponseProduct(String response) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(response);
			return null;
		} catch (Exception e) {
			return Mono.error(e);
		}
	}

}
