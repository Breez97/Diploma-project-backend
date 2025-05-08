package com.breez.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.breez.constants.Constants.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CombinedSearchRequest {

	@NotBlank(message = "Title can't be empty")
	private String title;

	@Pattern(regexp = "^(popular|new|priceasc|pricedesc|rating)$", message = "Invalid sort value")
	private String sort = DEFAULT_SORT;

	@NotNull(message = "Chunk can't be null")
	@Min(value = 1, message = "Chunk can't be negative or zero")
	private Integer chunk = DEFAULT_CHUNK;

	@Pattern(regexp = "^(ozon|wildberries)(,(ozon|wildberries))*$", message = "Invalid value for marketplaces")
	@NotBlank(message = "Marketplaces can't be empty")
	private String marketplaces = DEFAULT_MARKETPLACES;

}
