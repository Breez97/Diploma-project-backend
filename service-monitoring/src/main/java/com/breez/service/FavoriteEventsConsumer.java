package com.breez.service;

import com.breez.dto.event.FavoritesEventDto;
import org.springframework.messaging.handler.annotation.Payload;

public interface FavoriteEventsConsumer {

	void handleFavoritesEvent(@Payload FavoritesEventDto event);

}
