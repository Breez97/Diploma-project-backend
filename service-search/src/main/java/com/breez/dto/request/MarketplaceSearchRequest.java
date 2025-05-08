package com.breez.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.breez.constants.Constants.DEFAULT_CHUNK;
import static com.breez.constants.Constants.DEFAULT_SORT;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketplaceSearchRequest {

	@NotBlank(message = "Title can't be empty")
	private String title;

	@Pattern(regexp = "^(popular|new|priceasc|pricedesc|rating)$", message = "Invalid sort value")
	private String sort = DEFAULT_SORT;

	@NotNull(message = "Chunk can't be null")
	@Min(value = 1, message = "Chunk can't be negative or zero")
	private Integer chunk = DEFAULT_CHUNK;

}
