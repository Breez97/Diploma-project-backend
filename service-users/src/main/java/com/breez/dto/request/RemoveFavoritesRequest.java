package com.breez.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RemoveFavoritesRequest {

	@NotNull(message = "ItemId can't be null")
	@Positive(message = "ItemId must be positive number")
	private Long itemId;

	@NotNull(message = "MarketplaceSource can't be null")
	private String marketplaceSource;

}
