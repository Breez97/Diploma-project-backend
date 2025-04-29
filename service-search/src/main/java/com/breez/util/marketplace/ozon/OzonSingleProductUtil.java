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

@Component
public class OzonSingleProductUtil extends OzonUtil {

	private static final Logger logger = LoggerFactory.getLogger(OzonSingleProductUtil.class);

	public Map<String, Object> getSingleProductFromResponse(String response, long id) {
		if (StringUtils.isBlank(response)) {
			return null;
		}

		try {
			JsonNode rootNode = ObjectMapperSingleton.getInstance().readTree(response);
			return getProductInfo(rootNode, id).orElse(null);
		} catch (Exception e) {
			logger.error("Ozon getSingleProductFromResponse error={}", e.getMessage());
			throw new DataParsingException(HttpStatus.INTERNAL_SERVER_ERROR, "Ozon: " + e.getMessage());
		}
	}

	private Optional<Map<String, Object>> getProductInfo(JsonNode rootNode, long id) {
		return extractInnerHtmlNode(rootNode)
				.flatMap(this::parseInnerHtmlNode)
				.map(innerHtmlNode -> extractProductInfo(innerHtmlNode, rootNode, id));
	}

	private Optional<String> extractInnerHtmlNode(JsonNode rootNode) {
		return Optional.ofNullable(rootNode)
				.map(node -> node.path("seo").path("script").elements())
				.filter(Iterator::hasNext)
				.map(iterator -> iterator.next().path("innerHTML").asText());
	}

	private Optional<JsonNode> parseInnerHtmlNode(String innerHtmlText) {
		try {
			return Optional.of(ObjectMapperSingleton.getInstance().readTree(innerHtmlText));
		} catch (IOException e) {
			logger.error("Ozon: parseInnerHtml exception={}", e.getMessage());
			throw new DataParsingException(HttpStatus.INTERNAL_SERVER_ERROR, "Ozon: " + e.getMessage());
		}
	}

	private Map<String, Object> extractProductInfo(JsonNode innerHtmlNode, JsonNode rootNode, long id) {
		Map<String, Object> productData = getEmptyData(id);
		String externalLink = getExternalLinkOzon(id);
		String title = extractTitle(innerHtmlNode);
		String imageUrl = extractImageUrl(rootNode);
		String brand = extractBrand(innerHtmlNode);
		String price = extractPrice(rootNode);
		String rating = extractRating(innerHtmlNode);
		String feedbacks = extractFeedbacks(innerHtmlNode);
		String description = extractDescription(innerHtmlNode);
		List<Object> options = extractOptions(rootNode);
		productData.put("id", id);
		productData.put("externalLink", stringOrNull(externalLink));
		productData.put("title", stringOrNull(title));
		productData.put("imageUrl", stringOrNull(imageUrl));
		productData.put("brand", stringOrNull(brand));
		productData.put("price", stringOrNull(price));
		productData.put("rating", stringOrNull(rating));
		productData.put("feedbacks", stringOrNull(feedbacks));
		productData.put("description", stringOrNull(description));
		productData.put("options", listOrNull(options));
		return productData;
	}

	private String extractTitle(JsonNode innerHtmlNode) {
		return Optional.ofNullable(innerHtmlNode)
				.map(nameNode -> nameNode.path("name"))
				.map(JsonNode::asText)
				.map(this::capitalizeFirstLetter)
				.orElse(null);
	}

	private String extractImageUrl(JsonNode rootNode) {
		Optional<String> gallery = Optional.ofNullable(rootNode)
				.map(widgetStatesNode -> widgetStatesNode.path("widgetStates"))
				.map(webGalleryNode -> webGalleryNode.path("webGallery-3311626-default-1"))
				.map(JsonNode::asText);
		if (gallery.isPresent()) {
			try {
				JsonNode galleryNode = ObjectMapperSingleton.getInstance().readTree(gallery.get());
				return Optional.ofNullable(galleryNode)
						.map(coverImageNode -> coverImageNode.path("coverImage"))
						.map(JsonNode::asText)
						.orElse(null);
			} catch (IOException e) {
				logger.error("Ozon: extractImageUrl exception={}", e.getMessage());
				throw new DataParsingException(HttpStatus.INTERNAL_SERVER_ERROR, "Ozon: " + e.getMessage());
			}
		}
		return null;
	}

	private String extractBrand(JsonNode innerHtmlNode) {
		return Optional.ofNullable(innerHtmlNode)
				.map(brandNode -> brandNode.path("brand"))
				.map(JsonNode::asText)
				.map(this::capitalizeFirstLetter)
				.orElse(null);
	}

	private String extractPrice(JsonNode rootNode) {
		Optional<String> webAspects = Optional.ofNullable(rootNode)
				.map(widgetStatesNode -> widgetStatesNode.path("widgetStates"))
				.map(webAspectsNode -> webAspectsNode.path("webAspects-3529295-default-1"))
				.map(JsonNode::asText);
		return webAspects.map(this::extractPriceFromWebAspects).orElse(null);
	}

	private String extractPriceFromWebAspects(String webAspects) {
		try {
			JsonNode webAspectsNode = ObjectMapperSingleton.getInstance().readTree(webAspects);
			Optional<JsonNode> variantsNode = Optional.ofNullable(webAspectsNode)
					.map(aspectsNode -> aspectsNode.path("aspects"))
					.filter(JsonNode::isArray)
					.map(firstAspectNode -> firstAspectNode.get(0))
					.map(resultVariantNode -> resultVariantNode.path("variants"));
			return variantsNode.map(this::extractPriceFromVariantsNode).orElse(null);
		} catch (IOException e) {
			logger.info("Ozon: extractPrice exception={}", e.getMessage());
			throw new DataParsingException(HttpStatus.INTERNAL_SERVER_ERROR, "Ozon: " + e.getMessage());
		}
	}

	private String extractPriceFromVariantsNode(JsonNode variantsNode) {
		for (JsonNode variantNode : variantsNode) {
			Optional<String> variantText = Optional.ofNullable(variantNode)
					.map(activeNode -> activeNode.path("active"))
					.map(JsonNode::asText);
			if (variantText.isPresent() && "true".equals(variantText.get())) {
				return Optional.of(variantNode)
						.map(pathNode -> pathNode.path("data"))
						.map(priceNode -> priceNode.path("price"))
						.map(JsonNode::asText)
						.map(this::extractDigitsFromString)
						.orElse(null);
			}
		}
		return null;
	}

	private String extractRating(JsonNode innerHtmlNode) {
		return Optional.ofNullable(innerHtmlNode)
				.map(aggregateRatingNode -> aggregateRatingNode.path("aggregateRating"))
				.map(ratingValueNode -> ratingValueNode.path("ratingValue"))
				.map(JsonNode::asText)
				.orElse(null);
	}

	private String extractFeedbacks(JsonNode innerHtmlNode) {
		return Optional.ofNullable(innerHtmlNode)
				.map(aggregateRatingNode -> aggregateRatingNode.path("aggregateRating"))
				.map(reviewCountNode -> reviewCountNode.path("reviewCount"))
				.map(JsonNode::asText)
				.orElse(null);
	}

	private String extractDescription(JsonNode innerHtmlNode) {
		return Optional.ofNullable(innerHtmlNode)
				.map(descriptionNode -> descriptionNode.path("description"))
				.map(JsonNode::asText)
				.orElse(null);
	}

	private List<Object> extractOptions(JsonNode rootNode) {
		List<Object> optionsList = new LinkedList<>();
		try {
			Optional<String> webCharacteristics = Optional.ofNullable(rootNode)
					.map(widgetStates -> widgetStates.path("widgetStates"))
					.map(characteristicsNode -> characteristicsNode.path("webShortCharacteristics-3385952-default-1"))
					.map(JsonNode::asText);
			if (webCharacteristics.isPresent()) {
				JsonNode webCharacteristicsNode = ObjectMapperSingleton.getInstance().readTree(webCharacteristics.get());
				Optional<JsonNode> characteristicsNode = Optional.ofNullable(webCharacteristicsNode)
						.map(characteristics -> characteristics.path("characteristics"))
						.filter(JsonNode::isArray);
				if (characteristicsNode.isPresent()) {
					for (JsonNode characteristicNode : characteristicsNode.get()) {
						Map<String, Object> infoMap = new LinkedHashMap<>();
						String name = characteristicNode.path("title").path("textRs").get(0).path("content").asText();
						String value = characteristicNode.path("values").get(0).path("text").asText();
						infoMap.put("name", name);
						infoMap.put("value", value);
						optionsList.add(infoMap);
					}
				}
			}
		} catch (IOException e) {
			logger.error("Ozon: extractOptions error={}", e.getMessage());
			throw new DataParsingException(HttpStatus.INTERNAL_SERVER_ERROR, "Ozon: " + e.getMessage());
		}
		return optionsList;
	}

}
