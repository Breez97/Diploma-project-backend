package com.breez.service;

import com.breez.component.WebClientFactory;
import com.breez.exception.ClientException;
import com.breez.exception.DataParsingException;
import com.breez.exception.ServerException;
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

@Service
@RequiredArgsConstructor
public class WildberriesService implements SearchService {

	private static final String WILDBERRIES_BASE_URL = "https://search.wb.ru";
	private static final int WILDBERRIES_PAGE = 1;
	private static final String WILDBERRIES_SEX = "common";
	private static final String WILDBERRIES_SORT = "popular";
	private static final int WILDBERRIES_AMOUNT = 30;
	private static final boolean WILDBERRIES_SPELL_CHECK = true;

	private final WebClientFactory webClientFactory;

	public Mono<List<Map<String, Object>>> wildberriesFetchData(String title) {
		return wildberriesFetchData(title, WILDBERRIES_PAGE, WILDBERRIES_SEX, WILDBERRIES_SORT, WILDBERRIES_AMOUNT, WILDBERRIES_SPELL_CHECK);
	}

	public Mono<List<Map<String, Object>>> wildberriesFetchData(String title, int page, String sex, String sort, int amount, boolean spellCheck) {
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
						.queryParam("suppressSpellcheck", spellCheck)
						.build())
				.header("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36")
				.header("x-captcha-id", "Catalog 1|1|1744456262|AA==|3dead74d4f3f4973b5759f195d0702cd|6l373ADvEwKGWgqwMDeLv70lnbtSaRtuUrIzoHGpIiN")
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
				.map(buffer -> {
					byte[] bytes = new byte[buffer.readableByteCount()];
					buffer.read(bytes);
					return new String(bytes, StandardCharsets.UTF_8);
				})
				.reduce((str1, str2) -> str1 + str2)
				.flatMap(this::processResponse)
				.onErrorMap(e -> new DataParsingException(HttpStatus.INTERNAL_SERVER_ERROR, "Wildberries: " + e.getMessage()));
	}

	private Mono<List<Map<String, Object>>> processResponse(String response) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(response);
			JsonNode productsNode = rootNode.path("data").path("products");

			List<Map<String, Object>> productsList = new ArrayList<>();
			for (JsonNode productNode : productsNode) {
				Map<String, Object> productData = new HashMap<>();
				long id = productNode.path("id").asLong();
				productData.put("id", id);
				productData.put("name", productNode.path("name").asText());
				productData.put("imageUrl", getImageUrl(id));
				productData.put("brand", productNode.path("brand").asText());
				productData.put("price", productNode.path("sizes").get(0).path("price").path("product").asInt() / 100);
				productsList.add(productData);
			}
			return Mono.just(productsList);
		} catch (Exception e) {
			return Mono.error(e);
		}
	}

	private String getImageUrl(long id) {
		long vol = id / 100000;
		long part = id / 1000;
		String basketNum = getBasketNum(vol);

		return String.format("https://basket-%s.wbbasket.ru/vol%d/part%d/%d/images/big/1.webp", basketNum, vol, part, id);
	}

	private Mono<List<Map<String, Object>>> wildberriesProductFetchData(int id) {
		// Url информации по товару
		// https://basket-03.wbbasket.ru/vol370/part37003/37003175/info/ru/card.json
		return null;
	}

	private String getBasketNum(long vol) {
		// Список серверов баскетов
		// https://static-basket-01.wbbasket.ru/vol2/site/j/spa/index.7f239ace2fcf8a7bb122.js
		if (vol <= 143) return "01";
		else if (vol <= 287) return "02";
		else if (vol <= 431) return "03";
		else if (vol <= 719) return "04";
		else if (vol <= 1007) return "05";
		else if (vol <= 1061) return "06";
		else if (vol <= 1115) return "07";
		else if (vol <= 1169) return "08";
		else if (vol <= 1313) return "09";
		else if (vol <= 1601) return "10";
		else if (vol <= 1655) return "11";
		else if (vol <= 1919) return "12";
		else if (vol <= 2045) return "13";
		else if (vol <= 2189) return "14";
		else if (vol <= 2405) return "15";
		else if (vol <= 2621) return "16";
		else if (vol <= 2837) return "17";
		else if (vol <= 3053) return "18";
		else if (vol <= 3269) return "19";
		else if (vol <= 3485) return "20";
		else if (vol <= 3701) return "21";
		else if (vol <= 3917) return "22";
		else if (vol <= 4133) return "23";
		else if (vol <= 4349) return "24";
		else if (vol <= 4565) return "25";
		else return "26";
	}

}
