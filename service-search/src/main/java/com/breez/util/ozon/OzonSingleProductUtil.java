package com.breez.util.ozon;

import com.breez.exception.DataParsingException;
import com.breez.mapper.ObjectMapperSingleton;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class OzonSingleProductUtil extends OzonUtil {

	public Map<String, Object> getSingleProductFromResponse(String response, long id) {
		if (StringUtils.isBlank(response)) {
			return null;
		}

		try {
			JsonNode rootNode = ObjectMapperSingleton.getInstance().readTree(response);
			return getProductInfo(rootNode, id).orElse(null);
		} catch (Exception e) {
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
			throw new DataParsingException(HttpStatus.INTERNAL_SERVER_ERROR, "Ozon: " + e.getMessage());
		}
	}

	private Map<String, Object> extractProductInfo(JsonNode innerHtmlNode, JsonNode rootNode, long id) {
		Map<String, Object> productData = getEmptyDataSingleProduct(id);
		String externalLink = getExternalLinkOzon(id);
		String title = extractTitle(innerHtmlNode);
		String imageUrl = extractImageUrl(rootNode);
		String description = extractDescription(innerHtmlNode);
		List<Object> options = extractOptions(rootNode);
		productData.put("id", id);
		productData.put("externalLink", stringOrNull(externalLink));
		productData.put("title", stringOrNull(title));
		productData.put("imageUrl", stringOrNull(imageUrl));
		productData.put("description", stringOrNull(description));
		productData.put("options", listOrNull(options));
		return productData;
	}

	private String extractTitle(JsonNode innerHtmlNode) {
		return Optional.ofNullable(innerHtmlNode)
				.map(nameNode -> nameNode.path("name"))
				.map(JsonNode::asText)
				.orElse(null);
	}

	private String extractImageUrl(JsonNode rootNode) {
		String gallery = rootNode.path("widgetStates").path("webGallery-3311626-default-1").asText();
		try {
			JsonNode galleryNode = ObjectMapperSingleton.getInstance().readTree(gallery);
			return Optional.ofNullable(galleryNode)
					.map(coverImageNode -> coverImageNode.path("coverImage"))
					.map(JsonNode::asText)
					.orElse(null);
		} catch (IOException e) {
			throw new DataParsingException(HttpStatus.INTERNAL_SERVER_ERROR, "Ozon: " + e.getMessage());
		}
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
			throw new DataParsingException(HttpStatus.INTERNAL_SERVER_ERROR, "Ozon: " + e.getMessage());
		}
		return optionsList;
	}

}
