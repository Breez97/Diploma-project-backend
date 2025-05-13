package com.breez.service;

import com.breez.dto.event.NotificationsEventDto;
import com.breez.dto.event.PriceAlertEventDto;

public interface NotificationsEventConsumer {

	void handleNotificationsEvent(NotificationsEventDto event);

	void handlerPriceAlertsEvents(PriceAlertEventDto event);

}
