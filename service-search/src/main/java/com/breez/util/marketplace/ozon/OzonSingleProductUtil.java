package com.breez.util.marketplace.ozon;

import com.breez.dto.ProductDetailsDto;
import com.breez.dto.ProductOptionDto;
import com.breez.exception.DataParsingException;
import com.breez.mapper.ObjectMapperSingleton;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class OzonSingleProductUtil extends OzonUtil {

	private static final Logger logger = LoggerFactory.getLogger(OzonSingleProductUtil.class);

	public ProductDetailsDto getSingleProductFromResponse(String response, long id) {
		if (StringUtils.isBlank(response)) {
			return null;
		}

		try {
			JsonNode rootNode = ObjectMapperSingleton.getInstance().readTree(response);
			return getProductInfo(rootNode, id).orElse(null);
		} catch (Exception e) {
			logger.error("Ozon getSingleProductFromResponse error={}", e.getMessage());
			throw new DataParsingException("Ozon: " + e.getMessage());
		}
	}

	private Optional<ProductDetailsDto> getProductInfo(JsonNode rootNode, long id) {
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
			throw new DataParsingException("Ozon: " + e.getMessage());
		}
	}

	private ProductDetailsDto extractProductInfo(JsonNode innerHtmlNode, JsonNode rootNode, long id) {
		String externalLink = getExternalLinkOzon(id);
		String title = extractTitle(innerHtmlNode);
		String imageUrl = extractImageUrl(rootNode);
		String brand = extractBrand(innerHtmlNode);
		String price = extractPrice(rootNode);
		String rating = extractRating(innerHtmlNode);
		String feedbacks = extractFeedbacks(innerHtmlNode);
		String description = extractDescription(innerHtmlNode);
		List<ProductOptionDto> options = extractOptions(rootNode);
		return ProductDetailsDto.builder()
				.id(id)
				.externalLink(stringOrNull(externalLink))
				.title(stringOrNull(title))
				.imageUrl(stringOrNull(imageUrl))
				.brand(stringOrNull(brand))
				.price(convertPriceStringToBigDecimalOrNull(price))
				.rating(stringOrNull(rating))
				.feedbacks(stringOrNull(feedbacks))
				.description(stringOrNull(description))
				.options(listOrNull(options))
				.build();
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
				throw new DataParsingException("Ozon: " + e.getMessage());
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
			throw new DataParsingException("Ozon: " + e.getMessage());
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

	private List<ProductOptionDto> extractOptions(JsonNode rootNode) {
		List<ProductOptionDto> optionsList = new LinkedList<>();
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
						String name = characteristicNode.path("title").path("textRs").get(0).path("content").asText();
						String value = characteristicNode.path("values").get(0).path("text").asText();
						String capitalizedName = capitalizeFirstLetter(name);
						String capitalizedValue = capitalizeFirstLetter(value);
						if (StringUtils.isNotBlank(capitalizedName) && StringUtils.isNotBlank(capitalizedValue)) {
							optionsList.add(ProductOptionDto.builder().name(capitalizedName).value(capitalizedValue).build());
						}
					}
				}
			}
		} catch (IOException e) {
			logger.error("Ozon: extractOptions error={}", e.getMessage());
			throw new DataParsingException("Ozon: " + e.getMessage());
		}
		return optionsList;
	}

}
