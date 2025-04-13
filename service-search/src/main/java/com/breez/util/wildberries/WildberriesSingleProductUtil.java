package com.breez.util.wildberries;

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
public class WildberriesSingleProductUtil extends WildberriesUtil {

	private static final Logger logger = LoggerFactory.getLogger(WildberriesSingleProductUtil.class);

	public Map<String, Object> getDescriptionAndOptionsFromResponse(String response, Map<String, Object> data) {
		if (StringUtils.isBlank(response)) {
			return null;
		}

		try {
			JsonNode rootNode = ObjectMapperSingleton.getInstance().readTree(response);
			return getProductInfo(rootNode, data);
		} catch (IOException e) {
			logger.error("Wildberries getDescriptionAndOptionsFromResponse error={}", e.getMessage());
			throw new DataParsingException(HttpStatus.INTERNAL_SERVER_ERROR, "Ozon: " + e.getMessage());
		}
	}

	private Map<String, Object> getProductInfo(JsonNode rootNode, Map<String, Object> data) {
		return Optional.ofNullable(rootNode)
				.map(dataNode -> dataNode.path("data"))
				.map(currentProduct -> extractProductInfo(rootNode, data))
				.orElse(Collections.emptyMap());
	}

	private Map<String, Object> extractProductInfo(JsonNode rootNode, Map<String, Object> data) {
		String description = extractDescription(rootNode);
		List<Object> options = extractOptions(rootNode);
		data.put("description", stringOrNull(description));
		data.put("options", listOrNull(options));
		return data;
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

}
