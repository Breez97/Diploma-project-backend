package com.breez.util.marketplace.ozon;

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

import static com.breez.constants.Constants.OZON;

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
				.map(widgetStatesNode -> widgetStatesNode.path("tileGridDesktop-3669724-default-2").asText())
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
					.map(topRightButtonsNode -> topRightButtonsNode.path("topRightButtons"))
					.filter(JsonNode::isArray)
					.map(favoriteProductMoleculeV2Node -> favoriteProductMoleculeV2Node.get(0).path("favoriteProductMoleculeV2"))
					.map(node -> node.path("id"))
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
		productData.put("marketplace", OZON);
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
					.map(typeNode -> typeNode.path("type"))
					.map(JsonNode::asText)
					.filter("textAtom"::equals)
					.flatMap(atomNode -> Optional.ofNullable(state.path("textAtom")))
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
			Optional<JsonNode> textNode = Optional.of(state)
					.filter(s -> "labelList".equals(s.path("type").asText(null)))
					.map(s -> s.path("labelList").path("items"))
					.filter(items -> items.isArray() && !items.isEmpty())
					.map(items -> items.get(0))
					.filter(firstItem -> "tile-list-paid-brand".equals(
							firstItem.path("testInfo").path("automatizationId").asText("")))
					.map(firstItem -> firstItem.path("title"));
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
					.filter(s -> "priceV2".equals(s.path("type").asText(null)))
					.map(s -> s.path("priceV2"))
					.map(priceV2Obj -> priceV2Obj.path("price"))
					.filter(priceArray -> priceArray.isArray() && !priceArray.isEmpty())
					.map(priceArray -> priceArray.get(0))
					.map(firstPriceObject -> firstPriceObject.path("text"))
					.filter(JsonNode::isTextual);
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
			if ("labelList".equals(state.path("type").asText(null))) {
				JsonNode itemsNode = state.path("labelList").path("items");
				if (itemsNode.isArray()) {
					for (JsonNode labelItem : itemsNode) {
						Optional<JsonNode> textNode = Optional.of(labelItem)
								.filter(item -> "tile-list-rating".equals(
										item.path("testInfo").path("automatizationId").asText("")))
								.map(item -> item.path("title"))
								.filter(JsonNode::isTextual);
						if (textNode.isPresent()) {
							return textNode;
						}
					}
				}
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
			if ("labelList".equals(state.path("type").asText(null))) {
				JsonNode itemsNode = state.path("labelList").path("items");
				if (itemsNode.isArray()) {
					for (JsonNode labelItem : itemsNode) {
						Optional<JsonNode> textNode = Optional.of(labelItem)
								.filter(item -> "tile-list-comments".equals(
										item.path("testInfo").path("automatizationId").asText("")))
								.map(item -> item.path("title"))
								.filter(JsonNode::isTextual);
						if (textNode.isPresent()) {
							return textNode;
						}
					}
				}
			}
		}
		return Optional.empty();
	}

}
