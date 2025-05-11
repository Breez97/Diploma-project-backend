package com.breez.service.implementation;

import com.breez.dto.event.FavoritesEventDto;
import com.breez.enums.ActionType;
import com.breez.service.ValidationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ValidationServiceImplementation implements ValidationService {

	private static final Set<String> AVAILABLE_ACTIONS = Set.of(ActionType.ADD.getValue(), ActionType.REMOVE.getValue());

	@Override
	public boolean isValidFavoriteChangeEventDto(FavoritesEventDto event) {
		if (event == null) {
			return false;
		}
		String email = event.getEmail();
		String marketplaceSource = event.getMarketplaceSource();
		String action = event.getAction().getValue();
		if (StringUtils.isBlank(email) || StringUtils.isBlank(marketplaceSource)) {
			return false;
		}
		if (!AVAILABLE_ACTIONS.contains(action)) {
			return false;
		}
		if (event.getItemId() == null) {
			return false;
		}
		return true;
	}

}
