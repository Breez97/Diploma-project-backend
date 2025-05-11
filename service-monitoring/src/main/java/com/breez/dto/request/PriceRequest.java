package com.breez.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PriceRequest {

	@NotNull(message = "Email can't be empty")
	private String email;

	@NotNull(message = "ItemId can't be null")
	@Min(value = 1, message = "Id can't be negative or zero")
	private Long itemId;

	@Pattern(regexp = "^(ozon|wildberries)$", message = "Invalid value for marketplaces")
	private String marketplaceSource;

}
