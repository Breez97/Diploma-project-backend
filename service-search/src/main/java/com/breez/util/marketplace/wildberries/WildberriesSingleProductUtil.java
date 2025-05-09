package com.breez.util.marketplace.wildberries;

import com.breez.dto.ProductDetailsDto;
import com.breez.dto.ProductDto;
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

import static com.breez.constants.Constants.WILDBERRIES;

@Component
public class WildberriesSingleProductUtil extends WildberriesUtil {

	private static final Logger logger = LoggerFactory.getLogger(WildberriesSingleProductUtil.class);

	public ProductDetailsDto getDescriptionAndOptionsFromResponse(String response, ProductDto productData) {
		if (StringUtils.isBlank(response)) {
			return null;
		}

		try {
			JsonNode rootNode = ObjectMapperSingleton.getInstance().readTree(response);
			return getProductInfo(rootNode, productData);
		} catch (IOException e) {
			logger.error("Wildberries getDescriptionAndOptionsFromResponse error={}", e.getMessage());
			throw new DataParsingException("Ozon: " + e.getMessage());
		}
	}

	private ProductDetailsDto getProductInfo(JsonNode rootNode, ProductDto productData) {
		return Optional.ofNullable(rootNode)
				.map(dataNode -> dataNode.path("data"))
				.map(currentProduct -> extractProductInfo(rootNode, productData))
				.orElse(null);
	}

	private ProductDetailsDto extractProductInfo(JsonNode rootNode, ProductDto productData) {
		String description = extractDescription(rootNode);
		List<ProductOptionDto> options = extractOptions(rootNode);
		return ProductDetailsDto.builder()
				.id(productData.getId())
				.externalLink(productData.getExternalLink())
				.title(productData.getTitle())
				.imageUrl(productData.getImageUrl())
				.brand(productData.getBrand())
				.price(productData.getPrice())
				.rating(productData.getRating())
				.feedbacks(productData.getFeedbacks())
				.description(stringOrNull(description))
				.options(listOrNull(options))
				.marketplace(WILDBERRIES)
				.build();
	}

	private String extractDescription(JsonNode rootNode) {
		return Optional.ofNullable(rootNode)
				.map(descriptionNode -> descriptionNode.path("description"))
				.map(JsonNode::asText)
				.map(this::stringOrNull)
				.orElse(null);
	}

	private List<ProductOptionDto> extractOptions(JsonNode rootNode) {
		return Optional.ofNullable(rootNode)
				.map(groupedOptionsNode -> groupedOptionsNode.path("grouped_options"))
				.filter(JsonNode::isArray)
				.map(this::convertOptionsNodeToList)
				.orElse(null);
	}

	private List<ProductOptionDto> convertOptionsNodeToList(JsonNode optionsNode) {
		List<ProductOptionDto> optionsList = new LinkedList<>();
		for (JsonNode optionNode : optionsNode) {
			Optional.ofNullable(optionNode)
					.map(node -> node.path("options"))
					.filter(JsonNode::isArray)
					.ifPresent(infosNode -> optionsList.addAll(convertInfoNodeToList(infosNode)));
		}
		return optionsList;
	}

	private List<ProductOptionDto> convertInfoNodeToList(JsonNode infosNode) {
		List<ProductOptionDto> optionsList = new LinkedList<>();
		for (JsonNode infoNode : infosNode) {
			if (infoNode != null) {
				Optional<String> nameOptional = Optional.ofNullable(infoNode.path("name")).map(JsonNode::asText);
				Optional<String> valueOptional = Optional.ofNullable(infoNode.path("value")).map(JsonNode::asText);
				if (nameOptional.isPresent() && valueOptional.isPresent()) {
					String capitalizedName = capitalizeFirstLetter(nameOptional.get());
					String capitalizedValue = capitalizeFirstLetter(valueOptional.get());
					if (StringUtils.isNotBlank(capitalizedName) && StringUtils.isNotBlank(capitalizedValue)) {
						optionsList.add(ProductOptionDto.builder().name(capitalizedName).value(capitalizedValue).build());
					}
				}
			}
		}
		return optionsList;
	}

}
