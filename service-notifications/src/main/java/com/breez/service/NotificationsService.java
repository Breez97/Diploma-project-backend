package com.breez.service;

import com.breez.dto.request.NotificationInfoRequest;
import com.breez.dto.request.NotificationToggleRequest;
import com.breez.dto.response.NotificationsResponse;

public interface NotificationsService {

	NotificationsResponse getNotificationInfo(NotificationInfoRequest request);

	NotificationsResponse toggleNotificationService(NotificationToggleRequest request);

}
