package com.breez.util;

import com.breez.exception.DataParsingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.breez.constants.Constants.OZON_BASE_URL;

@Component
public class OzonUtil extends CommonUtil {

	private static final String REGEX_CATEGORY = "/category/[^/]+/";
	private static final String REGEX_NUMBERS = "\\D";

	public String extractFirstCategoryValue(String jsonResponse) {
		if (jsonResponse == null || jsonResponse.isEmpty()) {
			return null;
		}

		Pattern pattern = Pattern.compile(REGEX_CATEGORY);
		Matcher matcher = pattern.matcher(jsonResponse);

		if (!matcher.find()) {
			return null;
		}
		do {
			String category = matcher.group();
			if (!category.contains("/supermarket")) {
				return category;
			}
		} while (matcher.find());

		return null;
	}

	public List<Map<String, Object>> extractResultProducts(String response) {
		if (StringUtils.isBlank(response)) {
			return null;
		}

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(response);
			JsonNode widgetStatesNode = rootNode.path("widgetStates");
			JsonNode searchResults = objectMapper.readTree(widgetStatesNode.path("searchResultsV2-3669723-default-2").asText());
			JsonNode productsNode = searchResults.path("items");

			List<Map<String, Object>> productsList = new ArrayList<>();
			for (JsonNode productNode : productsNode) {
				JsonNode idNode = productNode.path("skuId");
				if (idNode != null) {
					Map<String, Object> productData = extractProductData(productNode, idNode);
					productsList.add(productData);
				}
			}
			return productsList;

		} catch (JsonProcessingException e) {
			throw new DataParsingException(HttpStatus.INTERNAL_SERVER_ERROR, "Ozon: " + e.getMessage());
		}
	}

	private Map<String, Object> extractProductData(JsonNode productNode, JsonNode idNode) {
		Map<String, Object> productData = new LinkedHashMap<>();
		long id = idNode.asLong();
		JsonNode mainState = productNode.path("mainState");
		String title = extractTitle(mainState);
		JsonNode tileImage = productNode.path("tileImage");
		String imageUrl = extractImage(tileImage);
		String brand = extractBrand(mainState);
		String stringPrice = extractPrice(mainState);
		long price;
		if (stringPrice == null) {
			return null;
		} else {
			price = Long.parseLong(stringPrice);
		}
		JsonNode action = productNode.path("action");
		String productInfoLink = extractProductInfoLink(action);
		productData.put("id", id);
		productData.put("title", title);
		productData.put("imageUrl", imageUrl);
		productData.put("brand", brand);
		productData.put("price", price);
		productData.put("productInfoLink", productInfoLink);
		return productData;
	}

	private String extractTitle(JsonNode mainState) {
		if (mainState == null) {
			return null;
		}

		for (JsonNode state : mainState) {
			JsonNode stateId = state.path("id");
			if (stateId != null && "name".equals(stateId.asText())) {
				JsonNode textNode = state.path("atom").path("textAtom").path("text");
				if (textNode != null) {
					return capitalizeFirstLetter(textNode.asText());
				}
			}
		}
		return null;
	}

	private String extractImage(JsonNode tileImage) {
		if (tileImage == null) {
			return null;
		}

		JsonNode imageItems = tileImage.path("items");
		if (imageItems != null && imageItems.isArray() && !imageItems.isEmpty()) {
			JsonNode firstImage = imageItems.get(0).path("image").path("link");
			if (firstImage != null) {
				return firstImage.asText();
			}
		}
		return null;
	}

	private String extractBrand(JsonNode mainState) {
		if (mainState == null) {
			return null;
		}

		for (JsonNode state : mainState) {
			JsonNode atomNode = state.path("atom");
			if ("labelList".equals(atomNode.path("type").asText())) {
				for (JsonNode labelItem : atomNode.path("labelList").path("items")) {
					String title = labelItem.path("title").asText(null);
					if (title != null && title.contains("<b>")) {
						return capitalizeFirstLetter(title.replaceAll("<[^>]*>", "").trim());
					}
				}
			}
		}
		return null;
	}


	private String extractPrice(JsonNode mainState) {
		if (mainState == null) {
			return null;
		}

		for (JsonNode state : mainState) {
			JsonNode atomNode = state.path("atom");
			if (atomNode != null && "priceV2".equals(atomNode.path("type").asText())) {
				JsonNode priceNode = atomNode.path("priceV2").path("price");
				if (priceNode != null && priceNode.isArray() && !priceNode.isEmpty()) {
					JsonNode currentPriceNode = priceNode.get(0);
					if (currentPriceNode != null) {
						return extractDigitsFromPrice(currentPriceNode.path("text").asText());
					}
				}
			}
		}
		return null;
	}

	private String extractDigitsFromPrice(String price) {
		if (price == null || price.isEmpty()) {
			return null;
		}
		return price.replaceAll(REGEX_NUMBERS, "");
	}

	private String extractProductInfoLink(JsonNode action) {
		if (action == null) {
			return null;
		}

		return OZON_BASE_URL + action.path("link").asText();
	}

}
