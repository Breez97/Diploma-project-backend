package com.breez.dto.response;

import com.breez.dto.ProductDetailsDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceSearchResponseDto {

	private String timestamp;
	private String status;
	private String message;
	private ProductDetailsDto data;

}
