package com.breez.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistoryResponse {

	private String email;
	private Long itemId;
	private String marketplaceSource;
	private List<PriceDataEntryDto> priceHistory;

}
