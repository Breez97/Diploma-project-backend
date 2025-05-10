package com.breez.service.implementation;

import com.breez.dto.event.FavoritesEventDto;
import com.breez.enums.ActionType;
import com.breez.model.MonitoredItem;
import com.breez.repository.MonitoredItemRepository;
import com.breez.service.FavoriteEventsConsumer;
import com.breez.service.PriceUpdateScheduler;
import com.breez.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoriteEventsConsumerImplementation implements FavoriteEventsConsumer {

	private static final Logger logger = LoggerFactory.getLogger(FavoriteEventsConsumerImplementation.class);

	private final MonitoredItemRepository monitoredItemRepository;
	private final PriceUpdateScheduler priceUpdateScheduler;
	private final ValidationService validationService;

	@Override
	@Transactional
	@KafkaListener(topics = "${app.kafka.topic.user-favorites}", groupId = "${spring.kafka.consumer.group-id}")
	public void handleFavoritesEvent(@Payload FavoritesEventDto event) {
		if (!validationService.isValidFavoriteChangeEventDto(event)) {
			logger.warn("Received invalid favorite change event");
			return;
		}

		String email = event.getEmail();
		Long itemId = event.getItemId();
		String marketplaceSource = event.getMarketplaceSource();
		ActionType action = event.getAction();
		if (action == ActionType.ADD) {
			Optional<MonitoredItem> existingItem = monitoredItemRepository.findByEmailAndItemIdAndMarketplaceSource(email, itemId, marketplaceSource);
			if (existingItem.isEmpty()) {
				MonitoredItem newItem = MonitoredItem.builder()
						.email(email)
						.itemId(itemId)
						.marketplaceSource(marketplaceSource)
						.priceHistory(new ArrayList<>())
						.build();
				monitoredItemRepository.save(newItem);
				priceUpdateScheduler.updatePriceForSpecificItem(newItem);
				logger.info("Added new monitored item for email: {}, productId: {}, marketplace: {}", email, itemId, marketplaceSource);
			}
		} else if (action == ActionType.REMOVE) {
			monitoredItemRepository.deleteByEmailAndItemIdAndMarketplaceSource(email, itemId, marketplaceSource);
		}
	}

}
