package com.breez.util.wildberries;

import com.breez.mapper.ObjectMapperSingleton;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;

@Component
public class WildberriesSingleProductUtil extends WildberriesUtil {

	public Map<String, Object> getSingleProductFromResponse(String response) {
		if (StringUtils.isBlank(response)) {
			return null;
		}

		try {
			JsonNode rootNode = ObjectMapperSingleton.getInstance().readTree(response);
			return getProductInfo(rootNode);
		} catch (Exception e) {
			return null;
		}
	}

	private Map<String, Object> getProductInfo(JsonNode rootNode) {
		return Optional.ofNullable(rootNode)
				.map(nmIdNode -> nmIdNode.path("nm_id"))
				.map(idNode -> extractProductInfo(rootNode, idNode))
				.orElse(Collections.emptyMap());
	}

	private Map<String, Object> extractProductInfo(JsonNode rootNode, JsonNode idNode) {
		long id = idNode.asLong();
		Map<String, Object> productData = getEmptyDataSingleProduct(id);
		String externalLink = getExternalLinkWildberries(id);
		String title = extractTitle(rootNode);
		String imageUrl = getImageUrl(id);
		String description = extractDescription(rootNode);
		List<Object> options = extractOptions(rootNode);
		productData.put("id", id);
		productData.put("externalLink", stringOrNull(externalLink));
		productData.put("title", stringOrNull(title));
		productData.put("imageUrl", stringOrNull(imageUrl));
		productData.put("description", stringOrNull(description));
		productData.put("options", listOrNull(options));
		return productData;
	}

	private String extractTitle(JsonNode rootNode) {
		return Optional.ofNullable(rootNode)
				.map(imtNameNode -> imtNameNode.path("imt_name"))
				.map(JsonNode::asText)
				.map(this::capitalizeFirstLetter)
				.orElse(null);
	}

	private String extractDescription(JsonNode rootNode) {
		return Optional.ofNullable(rootNode)
				.map(descriptionNode -> descriptionNode.path("description"))
				.map(JsonNode::asText)
				.map(this::stringOrNull)
				.orElse(null);
	}

	private List<Object> extractOptions(JsonNode rootNode) {
		return Optional.ofNullable(rootNode)
				.map(groupedOptionsNode -> groupedOptionsNode.path("grouped_options"))
				.filter(JsonNode::isArray)
				.map(this::convertOptionsNodeToList)
				.orElse(null);
	}

	private List<Object> convertOptionsNodeToList(JsonNode optionsNode) {
		List<Object> optionsList = new LinkedList<>();
		for (JsonNode optionNode : optionsNode) {
			Optional.ofNullable(optionNode)
					.map(node -> node.path("options"))
					.filter(JsonNode::isArray)
					.ifPresent(infosNode -> optionsList.addAll(convertInfoNodeToList(infosNode)));
		}
		return optionsList;
	}

	private List<Map<String, String>> convertInfoNodeToList(JsonNode infosNode) {
		List<Map<String, String>> optionsList = new LinkedList<>();
		for (JsonNode infoNode : infosNode) {
			if (infoNode != null) {
				Optional<String> nameOptional = Optional.ofNullable(infoNode.path("name")).map(JsonNode::asText);
				Optional<String> valueOptional = Optional.ofNullable(infoNode.path("value")).map(JsonNode::asText);
				if (nameOptional.isPresent() && valueOptional.isPresent()) {
					Map<String, String> infoMap = new LinkedHashMap<>();
					infoMap.put("name", nameOptional.get());
					infoMap.put("value", valueOptional.get());
					optionsList.add(infoMap);
				}
			}
		}
		return optionsList;
	}

	public String getProductInfoLink(long id) {
		long vol = id / 100000;
		long part = id / 1000;
		String basketNum = getBasketNum(vol);

		return String.format("https://basket-%s.wbbasket.ru/vol%d/part%d/%d/info/ru/card.json", basketNum, vol, part, id);
	}

}
