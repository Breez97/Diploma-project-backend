package com.breez.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceAlertEventDto {

	private String email;
	private Long itemId;
	private String marketplaceSource;
	private String productName;
	private String productImageUrl;
	private String productUrl;
	private BigDecimal oldPrice;
	private BigDecimal newPrice;

}
