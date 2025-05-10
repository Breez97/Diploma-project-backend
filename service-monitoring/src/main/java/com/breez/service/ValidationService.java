package com.breez.service;

import com.breez.dto.event.FavoritesEventDto;

public interface ValidationService {

	boolean isValidFavoriteChangeEventDto(FavoritesEventDto event);

}
