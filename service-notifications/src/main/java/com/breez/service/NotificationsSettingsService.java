package com.breez.service;

import com.breez.dto.event.NotificationsEventDto;
import com.breez.dto.event.PriceAlertEventDto;

public interface NotificationsSettingsService {

	void processNotificationEvent(NotificationsEventDto event);

	void priceAlertEventDto(PriceAlertEventDto event);

}
