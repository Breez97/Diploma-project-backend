package com.breez.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

	private Long id;
	private String externalLink;
	private String title;
	private String imageUrl;
	private String brand;
	private String price;
	private String rating;
	private String feedbacks;
	private String marketplace;

}
