package com.breez.util.marketplace.wildberries;

import com.breez.dto.ProductDto;
import com.breez.exception.DataParsingException;
import com.breez.mapper.ObjectMapperSingleton;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.breez.constants.Constants.WILDBERRIES;

@Component
public class WildberriesAllProductsUtil extends WildberriesUtil {

	private static final Logger logger = LoggerFactory.getLogger(WildberriesAllProductsUtil.class);

	public List<ProductDto> getAllProductsFromResponse(String responseBody) {
		if (StringUtils.isBlank(responseBody)) {
			return null;
		}

		try {
			JsonNode rootNode = ObjectMapperSingleton.getInstance().readTree(responseBody);
			return extractProducts(rootNode);
		} catch (Exception e) {
			logger.error("Wildberries getAllProductsFromResponse error={}", e.getMessage());
			throw new DataParsingException("Ozon: " + e.getMessage());
		}
	}

	private List<ProductDto> extractProducts(JsonNode rootNode) {
		Optional<JsonNode> resultProductsNode = Optional.ofNullable(rootNode)
				.map(dataNode -> dataNode.path("data"))
				.map(productsNode -> productsNode.path("products"))
				.filter(JsonNode::isArray);
		if (resultProductsNode.isPresent()) {
			List<ProductDto> products = new LinkedList<>();
//			List<Map<String, Object>> productsList = new LinkedList<>();
			for (JsonNode productNode : resultProductsNode.get()) {
				Optional<Long> resultIdNode = Optional.ofNullable(productNode)
						.map(idNode -> idNode.path("id"))
						.map(JsonNode::asLong);
				if (resultIdNode.isPresent()) {
					Optional.of(resultIdNode)
							.map(currentProductNode -> extractProductInfoFromNode(resultIdNode.get(), productNode))
							.ifPresent(products::add);
				}
			}
			return products;
		}
		return null;
	}

	public ProductDto getProductInfoFromResponse(long id, String responseBody) {
		if (StringUtils.isBlank(responseBody)) {
			return null;
		}

		try {
			JsonNode rootNode = ObjectMapperSingleton.getInstance().readTree(responseBody);
			return extractProductInfo(id, rootNode);
		} catch (Exception e) {
			logger.error("Wildberries: getProductInfoFromResponse error={}", e.getMessage());
			throw new DataParsingException("Ozon: " + e.getMessage());
		}
	}

	private ProductDto extractProductInfo(long id, JsonNode rootNode) {
		return Optional.ofNullable(rootNode)
				.map(dataNode -> dataNode.path("data"))
				.map(productNode -> productNode.path("products"))
				.filter(JsonNode::isArray)
				.map(currentProductFromArrayNode -> currentProductFromArrayNode.get(0))
				.map(currentProductNode -> extractProductInfoFromNode(id, currentProductNode))
				.orElse(null);
	}

	private ProductDto extractProductInfoFromNode(long id, JsonNode currentProductNode) {
		String externalLink = getExternalLinkWildberries(id);
		String title = extractTitle(currentProductNode);
		String imageUrl = getImageUrl(id);
		String brand = extractBrand(currentProductNode);
		String price = extractPrice(currentProductNode);
		String rating = extractRating(currentProductNode);
		String feedbacks = extractFeedbacks(currentProductNode);
		return ProductDto.builder()
				.id(id)
				.externalLink(stringOrNull(externalLink))
				.title(stringOrNull(title))
				.imageUrl(stringOrNull(imageUrl))
				.brand(stringOrNull(brand))
				.price(convertPriceStringToBigDecimalOrNull(price))
				.rating(stringOrNull(rating))
				.feedbacks(stringOrNull(feedbacks))
				.marketplace(WILDBERRIES)
				.build();
	}

	private String extractTitle(JsonNode currentProductNode) {
		return Optional.ofNullable(currentProductNode)
				.map(nameNode -> nameNode.path("name"))
				.map(JsonNode::asText)
				.map(this::capitalizeFirstLetter)
				.orElse(null);
	}

	private String extractBrand(JsonNode currentProductNode) {
		return Optional.ofNullable(currentProductNode)
				.map(brandNode -> brandNode.path("brand"))
				.map(JsonNode::asText)
				.map(this::capitalizeFirstLetter)
				.orElse(null);
	}

	private String extractPrice(JsonNode currentProductNode) {
		return Optional.ofNullable(currentProductNode)
				.map(sizesNode -> sizesNode.path("sizes"))
				.map(this::findMinPrice)
				.map(this::reducePriceByTwoPercent)
				.orElse(null);
	}

	private String findMinPrice(JsonNode sizesNode) {
		Iterator<JsonNode> elements = sizesNode.elements();
		BigDecimal minPrice = null;

		while (elements.hasNext()) {
			JsonNode sizeNode = elements.next();
			JsonNode priceNode = sizeNode.path("price").path("total");
			String priceText = priceNode.asText();
			try {
				BigDecimal priceValue = new BigDecimal(priceText);
				if (minPrice == null || priceValue.compareTo(minPrice) < 0) {
					minPrice = priceValue;
				}
			} catch (NumberFormatException e) {
				logger.error("Wildberries: findMinPrice error={}", e.getMessage());
			}
		}
		return minPrice != null ? minPrice.toString() : null;
	}

	private String extractRating(JsonNode currentProductNode) {
		return Optional.ofNullable(currentProductNode)
				.map(reviewNode -> reviewNode.path("reviewRating"))
				.map(JsonNode::asText)
				.orElse(null);
	}

	private String extractFeedbacks(JsonNode currentProductNode) {
		return Optional.ofNullable(currentProductNode)
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

	private String reducePriceByTwoPercent(String price) {
		try {
			BigDecimal priceValue = new BigDecimal(price);
			priceValue = priceValue.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
			BigDecimal reducedPrice = priceValue.multiply(BigDecimal.valueOf(0.9797)).setScale(0, RoundingMode.HALF_DOWN);
			return reducedPrice.toString();
		} catch (NumberFormatException e) {
			return price;
		}
	}

}
