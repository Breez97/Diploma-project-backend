package com.breez.util.wildberries;

import com.breez.exception.DataParsingException;
import com.breez.mapper.ObjectMapperSingleton;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WildberriesAllProductsUtil extends WildberriesUtil {

	public List<Map<String, Object>> getAllProductsFromResponse(String response) {
		if (StringUtils.isBlank(response)) {
			return null;
		}

		try {
			JsonNode rootNode = ObjectMapperSingleton.getInstance().readTree(response);
			return getProductsList(rootNode);
		} catch (Exception e) {
			throw new DataParsingException(HttpStatus.INTERNAL_SERVER_ERROR, "Ozon: " + e.getMessage());
		}
	}

	private List<Map<String, Object>> getProductsList(JsonNode rootNode) {
		return Optional.ofNullable(rootNode)
				.map(dataProductsNode -> dataProductsNode.path("data").path("products"))
				.filter(JsonNode::isArray)
				.map(this::convertProductsNodeToList)
				.orElse(Collections.emptyList());
	}

	private List<Map<String, Object>> convertProductsNodeToList(JsonNode productsNode) {
		List<Map<String, Object>> productsList = new LinkedList<>();
		for (JsonNode productNode : productsNode) {
			Optional.ofNullable(productNode)
					.map(node -> node.path("id"))
					.map(idNode -> extractProduct(productNode, idNode))
					.ifPresent(productsList::add);
		}
		return productsList;
	}

	private Map<String, Object> extractProduct(JsonNode productNode, JsonNode idNode) {
		long id = idNode.asLong();
		Map<String, Object> productData = getEmptyDataAllProducts(id);
		String externalLink = getExternalLinkWildberries(id);
		String title = extractTitle(productNode);
		String imageUrl = getImageUrl(id);
		String brand = extractBrand(productNode);
		String price = extractPrice(productNode);
		String rating = extractRating(productNode);
		String feedbacks = extractFeedbacks(productNode);
		productData.put("id", id);
		productData.put("externalLink", stringOrNull(externalLink));
		productData.put("title", stringOrNull(title));
		productData.put("imageUrl", stringOrNull(imageUrl));
		productData.put("brand", stringOrNull(brand));
		productData.put("price", stringOrNull(price));
		productData.put("rating", stringOrNull(rating));
		productData.put("feedbacks", stringOrNull(feedbacks));
		return productData;
	}

	private String extractTitle(JsonNode productNode) {
		return Optional.ofNullable(productNode)
				.map(nameNode -> nameNode.path("name"))
				.map(JsonNode::asText)
				.orElse(null);
	}

	private String extractBrand(JsonNode productNode) {
		return Optional.ofNullable(productNode)
				.map(brandNode -> brandNode.path("brand"))
				.map(JsonNode::asText)
				.map(this::capitalizeFirstLetter)
				.orElse(null);
	}

	private String extractPrice(JsonNode productNode) {
		return Optional.ofNullable(productNode)
				.map(sizesNode -> sizesNode.path("sizes"))
				.filter(JsonNode::isArray)
				.map(sizesNode -> sizesNode.get(0))
				.map(firstSizeNode -> firstSizeNode.path("price"))
				.map(priceNode -> priceNode.path("product"))
				.map(JsonNode::asText)
				.map(this::cutPrice)
				.orElse(null);
	}

	private String extractRating(JsonNode productNode) {
		return Optional.ofNullable(productNode)
				.map(reviewRatingNode -> reviewRatingNode.path("reviewRating"))
				.map(JsonNode::asText)
				.orElse(null);
	}

	private String extractFeedbacks(JsonNode productNode) {
		return Optional.ofNullable(productNode)
				.map(feedbacksNode -> feedbacksNode.path("feedbacks"))
				.map(JsonNode::asText)
				.orElse(null);
	}

	private String cutPrice(String currentPrice) {
		if (StringUtils.isBlank(currentPrice)) {
			return null;
		}
		if (currentPrice.length() > 2) {
			return currentPrice.substring(0, currentPrice.length() - 2);
		}
		return null;
	}

}
