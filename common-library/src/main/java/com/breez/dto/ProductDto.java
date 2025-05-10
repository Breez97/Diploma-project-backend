package com.breez.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDto {

	private Long id;
	private String externalLink;
	private String title;
	private String imageUrl;
	private String brand;
	private BigDecimal price;
	private String rating;
	private String feedbacks;
	private String marketplace;

}
