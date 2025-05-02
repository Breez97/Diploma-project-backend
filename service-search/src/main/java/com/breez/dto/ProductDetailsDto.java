package com.breez.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailsDto {

	private Long id;
	private String externalLink;
	private String title;
	private String imageUrl;
	private String brand;
	private String price;
	private String rating;
	private String feedbacks;
	private String description;
	private List<ProductOptionDto> options;
	private String marketplace;

}
