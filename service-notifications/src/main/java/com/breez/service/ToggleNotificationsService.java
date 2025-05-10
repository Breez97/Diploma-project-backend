package com.breez.service;

import com.breez.dto.request.ToggleNotificationRequest;
import com.breez.dto.response.ToggleNotificationsResponse;

public interface ToggleNotificationsService {

	ToggleNotificationsResponse toggleNotificationService(ToggleNotificationRequest request);

}
