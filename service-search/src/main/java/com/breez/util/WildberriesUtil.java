package com.breez.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class WildberriesUtil extends CommonUtil {

	public Mono<List<Map<String, Object>>> extractResultProducts(String response) {
		if (StringUtils.isBlank(response)) {
			return null;
		}

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(response);
			JsonNode productsNode = rootNode.path("data").path("products");

			List<Map<String, Object>> productsList = new ArrayList<>();
			for (JsonNode productNode : productsNode) {
				JsonNode idNode = productNode.path("id");
				if (idNode != null) {
					Map<String, Object> productData = extractProductData(productNode, idNode);
					productsList.add(productData);
				}
			}
			return Mono.just(productsList);
		} catch (Exception e) {
			return Mono.error(e);
		}
	}

	private Map<String, Object> extractProductData(JsonNode productNode, JsonNode idNode) {
		Map<String, Object> productData = new LinkedHashMap<>();
		long id = idNode.asLong();
		String title = capitalizeFirstLetter(productNode.path("name").asText());
		String imageUrl = getImageUrl(id);
		String brandNode = productNode.path("brand").asText();
		String brand = StringUtils.isBlank(brandNode) ? null : brandNode;
		long price = productNode.path("sizes").get(0).path("price").path("product").asInt() / 100;
		String productInfoLink = getProductInfoLink(id);
		productData.put("id", id);
		productData.put("title", title);
		productData.put("imageUrl", imageUrl);
		productData.put("brand", brand);
		productData.put("price", price);
		productData.put("productInfoLink", productInfoLink);
		return productData;
	}

	private String getImageUrl(long id) {
		long vol = id / 100000;
		long part = id / 1000;
		String basketNum = getBasketNum(vol);

		return String.format("https://basket-%s.wbbasket.ru/vol%d/part%d/%d/images/big/1.webp", basketNum, vol, part, id);
	}

	public String getProductInfoLink(long id) {
		long vol = id / 100000;
		long part = id / 1000;
		String basketNum = getBasketNum(vol);

		return String.format("https://basket-%s.wbbasket.ru/vol%d/part%d/%d/info/ru/card.json", basketNum, vol, part, id);
	}

	private String getBasketNum(long vol) {
		// List of current baskets
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
