package com.breez.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDetailsDto {

	private Long id;
	private String externalLink;
	private String title;
	private String imageUrl;
	private String brand;
	private BigDecimal price;
	private String rating;
	private String feedbacks;
	private String description;
	private List<ProductOptionDto> options;
	private String marketplace;

}
