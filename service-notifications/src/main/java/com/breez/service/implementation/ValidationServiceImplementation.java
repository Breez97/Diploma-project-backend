package com.breez.service.implementation;

import com.breez.dto.event.PriceAlertEventDto;
import com.breez.service.ValidationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

import static com.breez.constants.Constants.*;
import static com.breez.constants.Constants.DEFAULT_LINK;
import static com.breez.constants.Constants.MARKETPLACE_SOURCE_OZON;
import static com.breez.constants.Constants.OZON_URL;
import static com.breez.constants.Constants.WILDBERRIES_URL;

@Service
public class ValidationServiceImplementation implements ValidationService {

	@Override
	public PriceAlertEventDto validatePriceAlertEvent(PriceAlertEventDto event) {
		String validatedProductName = Optional.ofNullable(event.getProductName())
				.filter(StringUtils::isNotBlank)
				.orElse("Нет названия");
		String validatedProductImageUrl = Optional.ofNullable(event.getProductImageUrl())
				.filter(StringUtils::isNotBlank)
				.orElse(PLACEHOLDER_IMAGE_URL);
		String validatedMarketplaceSource = Optional.ofNullable(event.getMarketplaceSource())
				.filter(AVAILABLE_MARKETPLACE_SOURCES::contains)
				.orElse("Нет названия маркетплейса");
		String validatedProductUrl = Optional.ofNullable(event.getProductUrl())
				.filter(StringUtils::isNotBlank)
				.orElseGet(() -> getDefaultProductUrl(validatedMarketplaceSource));
		BigDecimal validatedOldPrice = Optional.ofNullable(event.getOldPrice())
				.orElse(BigDecimal.ZERO);
		BigDecimal validatedNewPrice = Optional.ofNullable(event.getNewPrice())
				.orElse(BigDecimal.ZERO);
		return PriceAlertEventDto.builder()
				.email(event.getEmail())
				.itemId(event.getItemId())
				.marketplaceSource(validatedMarketplaceSource)
				.productName(validatedProductName)
				.productImageUrl(validatedProductImageUrl)
				.productUrl(validatedProductUrl)
				.oldPrice(validatedOldPrice)
				.newPrice(validatedNewPrice)
				.build();
	}

	private String getDefaultProductUrl(String marketplaceSource) {
		return switch (marketplaceSource) {
			case MARKETPLACE_SOURCE_WILDBERRIES -> WILDBERRIES_URL;
			case MARKETPLACE_SOURCE_OZON -> OZON_URL;
			default -> DEFAULT_LINK;
		};
	}

}
