package com.breez.service.implementation;

import com.breez.dto.request.NotificationInfoRequest;
import com.breez.dto.request.NotificationToggleRequest;
import com.breez.dto.response.NotificationsResponse;
import com.breez.enums.Notification;
import com.breez.exception.NotificationItemNotFoundException;
import com.breez.model.NotificationItem;
import com.breez.repository.NotificationItemRepository;
import com.breez.service.NotificationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationsServiceImplementation implements NotificationsService {

	private final NotificationItemRepository notificationItemRepository;

	@Override
	public NotificationsResponse getNotificationInfo(NotificationInfoRequest request) {
		String email = request.getEmail();
		Long itemId = request.getItemId();
		String marketplaceSource = request.getMarketplaceSource();
		NotificationItem item = notificationItemRepository.findByEmailAndItemIdAndMarketplaceSource(email, itemId, marketplaceSource)
				.orElseThrow(() -> new NotificationItemNotFoundException("Notification item not found"));
		return mapToResponse(item);
	}

	@Override
	@Transactional
	public NotificationsResponse toggleNotificationService(NotificationToggleRequest request) {
		String email = request.getEmail();
		Long itemId = request.getItemId();
		String marketplaceSource = request.getMarketplaceSource();
		String notification = request.getNotification();
		boolean isEnabled = Notification.ENABLE.getValue().equals(notification);
		NotificationItem item = notificationItemRepository.findByEmailAndItemIdAndMarketplaceSource(email, itemId, marketplaceSource)
				.orElseThrow(() -> new NotificationItemNotFoundException("Notification item not found"));
		item.setNotificationsEnabled(isEnabled);
		NotificationItem updatedItem = notificationItemRepository.save(item);
		return mapToResponse(updatedItem);
	}

	private NotificationsResponse mapToResponse(NotificationItem item) {
		return NotificationsResponse.builder()
				.itemId(item.getItemId())
				.marketplaceSource(item.getMarketplaceSource())
				.isEnabled(item.isNotificationsEnabled())
				.build();
	}

}
