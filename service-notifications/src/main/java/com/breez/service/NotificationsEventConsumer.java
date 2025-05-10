package com.breez.service;

import com.breez.dto.event.NotificationsEventDto;

public interface NotificationsEventConsumer {

	void handleNotificationsEvent(NotificationsEventDto event);

}
