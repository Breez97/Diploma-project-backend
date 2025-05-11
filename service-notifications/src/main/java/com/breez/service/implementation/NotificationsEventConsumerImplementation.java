package com.breez.service.implementation;

import com.breez.dto.event.NotificationsEventDto;
import com.breez.dto.event.PriceAlertEventDto;
import com.breez.service.NotificationsSettingsService;
import com.breez.service.NotificationsEventConsumer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationsEventConsumerImplementation implements NotificationsEventConsumer {

	private static final Logger logger = LoggerFactory.getLogger(NotificationsEventConsumerImplementation.class);
	private final NotificationsSettingsService notificationsSettingsService;

	@Override
	@KafkaListener(topics = "${app.kafka.topic.user-notifications}", groupId = "${spring.kafka.consumer.group-id}")
	public void handleNotificationsEvent(NotificationsEventDto event) {
		notificationsSettingsService.processNotificationEvent(event);
	}

	@KafkaListener(topics = "${app.kafka.topic.user-price-alerts}", groupId = "${spring.kafka.consumer.group-id}-alerts")
	public void handlerPriceAlertsEvents(PriceAlertEventDto event) {
		notificationsSettingsService.priceAlertEventDto(event);
	}

}
