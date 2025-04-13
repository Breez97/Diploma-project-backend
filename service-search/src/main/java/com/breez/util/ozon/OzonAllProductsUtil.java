package com.breez.util.ozon;

import com.breez.exception.DataParsingException;
import com.breez.mapper.ObjectMapperSingleton;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class OzonAllProductsUtil extends OzonUtil {

	private static final Logger logger = LoggerFactory.getLogger(OzonAllProductsUtil.class);

	private static final String REGEX_TAGS = "<[^>]*>";

	public List<Map<String, Object>> getAllProductsFromResponse (String response) {
		if (StringUtils.isBlank(response)) {
			return null;
		}

		try {
			JsonNode rootNode = ObjectMapperSingleton.getInstance().readTree(response);
			JsonNode productsNode = getProductNode(rootNode);
			return convertProductsNodeToList(productsNode);
		} catch (Exception e) {
			logger.error("Ozon: getAllProductsFromResponse error={}", e.getMessage());
			throw new DataParsingException(HttpStatus.INTERNAL_SERVER_ERROR, "Ozon: " + e.getMessage());
		}
	}

	private JsonNode getProductNode(JsonNode rootNode) {
		return Optional.of(rootNode)
				.map(node -> node.path("widgetStates"))
				.map(widgetStatesNode -> widgetStatesNode.path("searchResultsV2-3669723-default-2").asText())
				.map(searchResultsText -> {
					try {
						return ObjectMapperSingleton.getInstance().readTree(searchResultsText);
					} catch (IOException e) {
						logger.info("Ozon: getProductNode error={}", e.getMessage());
						throw new DataParsingException(HttpStatus.INTERNAL_SERVER_ERROR, "Ozon: " + e.getMessage());
					}
				})
				.map(searchResultsNode -> searchResultsNode.path("items"))
				.orElse(null);
	}

	private List<Map<String, Object>> convertProductsNodeToList(JsonNode productsNode) {
		List<Map<String, Object>> productsList = new LinkedList<>();
		for (JsonNode productNode : productsNode) {
			Optional.ofNullable(productNode)
					.map(node -> node.path("skuId"))
					.map(idNode -> extractProduct(productNode, idNode))
					.ifPresent(productsList::add);
		}
		return productsList;
	}

	private Map<String, Object> extractProduct(JsonNode productNode, JsonNode idNode) {
		long id = idNode.asLong();
		Map<String, Object> productData = getEmptyData(id);
		String externalLink = getExternalLinkOzon(id);
		String title = extractTitle(productNode);
		String imageUrl = extractImageUrl(productNode);
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
				.map(mainStateNode -> mainStateNode.path("mainState"))
				.flatMap(this::findTitleInNode)
				.map(JsonNode::asText)
				.map(this::capitalizeFirstLetter)
				.orElse(null);
	}

	private Optional<JsonNode> findTitleInNode(JsonNode mainState) {
		for (JsonNode state : mainState) {
			Optional<JsonNode> textNode = Optional.ofNullable(state)
					.map(idNode -> idNode.path("id"))
					.map(JsonNode::asText)
					.filter("name"::equals)
					.flatMap(idNode -> Optional.ofNullable(state.path("atom")))
					.flatMap(atomNode -> Optional.ofNullable(atomNode.path("textAtom")))
					.map(textAtomNode -> textAtomNode.path("text"));
			if (textNode.isPresent()) {
				return textNode;
			}
		}
		return Optional.empty();
	}

	private String extractImageUrl(JsonNode productNode) {
		return Optional.ofNullable(productNode)
				.map(tileImageNode -> tileImageNode.path("tileImage"))
				.map(imageItemsNode -> imageItemsNode.path("items"))
				.filter(JsonNode::isArray)
				.map(firstImageNode -> firstImageNode.get(0).path("image").path("link"))
				.map(JsonNode::asText)
				.orElse(null);
	}

	private String extractBrand(JsonNode productNode) {
		return Optional.ofNullable(productNode)
				.map(mainStateNode -> mainStateNode.path("mainState"))
				.flatMap(this::findBrandInNode)
				.map(JsonNode::asText)
				.map(this::removeTagsBrand)
				.map(this::capitalizeFirstLetter)
				.orElse(null);
	}

	private Optional<JsonNode> findBrandInNode(JsonNode mainState) {
		for (JsonNode state : mainState) {
			Optional<JsonNode> textNode = Optional.ofNullable(state)
					.map(atomNode -> atomNode.path("atom"))
					.filter(atomNode -> "labelList".equals(atomNode.path("type").asText()))
					.flatMap(atomNode -> {
						for (JsonNode labelItem : atomNode.path("labelList").path("items")) {
							String title = labelItem.path("title").asText(null);
							if (title != null && title.contains("<b>")) {
								return Optional.of(labelItem.path("title"));
							}
						}
						return Optional.empty();
					});
			if (textNode.isPresent()) {
				return textNode;
			}
		}
		return Optional.empty();
	}

	private String removeTagsBrand(String brand) {
		return brand.replaceAll(REGEX_TAGS, "").trim();
	}

	private String extractPrice(JsonNode productNode) {
		return Optional.ofNullable(productNode)
				.map(mainState -> mainState.path("mainState"))
				.flatMap(this::findPriceInNode)
				.map(JsonNode::asText)
				.map(this::extractDigitsFromString)
				.orElse(null);
	}

	private Optional<JsonNode> findPriceInNode(JsonNode mainState) {
		for (JsonNode state : mainState) {
			Optional<JsonNode> textNode = Optional.ofNullable(state)
					.map(node -> node.path("atom"))
					.filter(atomNode -> "priceV2".equals(atomNode.path("type").asText()))
					.map(priceNode -> priceNode.path("priceV2").path("price"))
					.filter(JsonNode::isArray)
					.map(currentPriceNode -> currentPriceNode.get(0))
					.map(priceNode -> priceNode.path("text"));
			if (textNode.isPresent()) {
				return textNode;
			}
		}
		return Optional.empty();
	}

	private String extractRating(JsonNode productNode) {
		return Optional.ofNullable(productNode)
				.map(mainStateNode -> mainStateNode.path("mainState"))
				.flatMap(this::findReviewRatingInNode)
				.map(JsonNode::asText)
				.map(String::trim)
				.orElse(null);
	}

	private Optional<JsonNode> findReviewRatingInNode(JsonNode mainState) {
		for (JsonNode state : mainState) {
			Optional<JsonNode> textNode = Optional.ofNullable(state)
					.map(atomNode -> atomNode.path("atom"))
					.filter(atomNode -> "labelList".equals(atomNode.path("type").asText()))
					.flatMap(atomNode -> {
						for (JsonNode labelItem : atomNode.path("labelList").path("items")) {
							String testInfoId = labelItem.path("testInfo").path("automatizationId").asText();
							if ("tile-list-rating".equals(testInfoId)) {
								return Optional.of(labelItem.path("title"));
							}
						}
						return Optional.empty();
					});
			if (textNode.isPresent()) {
				return textNode;
			}
		}
		return Optional.empty();
	}

	private String extractFeedbacks(JsonNode productNode) {
		return Optional.ofNullable(productNode)
				.map(mainStateNode -> mainStateNode.path("mainState"))
				.flatMap(this::findFeedbacksInNode)
				.map(JsonNode::asText)
				.map(this::extractDigitsFromString)
				.orElse(null);
	}

	private Optional<JsonNode> findFeedbacksInNode(JsonNode mainState) {
		for (JsonNode state : mainState) {
			Optional<JsonNode> textNode = Optional.ofNullable(state)
					.map(atomNode -> atomNode.path("atom"))
					.filter(atomNode -> "labelList".equals(atomNode.path("type").asText()))
					.flatMap(atomNode -> {
						for (JsonNode labelItem : atomNode.path("labelList").path("items")) {
							String testInfoId = labelItem.path("testInfo").path("automatizationId").asText();
							if ("tile-list-comments".equals(testInfoId)) {
								return Optional.of(labelItem.path("title"));
							}
						}
						return Optional.empty();
					});
			if (textNode.isPresent()) {
				return textNode;
			}
		}
		return Optional.empty();
	}

}
