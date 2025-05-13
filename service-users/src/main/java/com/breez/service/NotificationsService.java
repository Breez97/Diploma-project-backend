package com.breez.service;

import com.breez.dto.request.NotificationInfoRequest;
import com.breez.dto.request.NotificationToggleRequest;
import com.breez.dto.response.NotificationResponse;

public interface NotificationsService {

	NotificationResponse getNotificationInfo(String email, NotificationInfoRequest request);

	NotificationResponse toggleNotifications(String email, NotificationToggleRequest request);

}
