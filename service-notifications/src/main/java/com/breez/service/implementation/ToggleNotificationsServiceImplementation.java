package com.breez.service.implementation;

import com.breez.dto.request.ToggleNotificationRequest;
import com.breez.dto.response.ToggleNotificationsResponse;
import com.breez.exception.NotificationItemNotFoundException;
import com.breez.model.NotificationItem;
import com.breez.repository.NotificationItemRepository;
import com.breez.service.ToggleNotificationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class ToggleNotificationsServiceImplementation implements ToggleNotificationsService {

	private final NotificationItemRepository notificationItemRepository;

	@Override
	@Transactional
	public ToggleNotificationsResponse toggleNotificationService(ToggleNotificationRequest request) {
		String email = request.getEmail();
		Long itemId = request.getItemId();
		String marketplaceSource = request.getMarketplaceSource();
		boolean isEnabled = request.isEnabled();
		NotificationItem item = notificationItemRepository.findByEmailAndItemIdAndMarketplaceSource(email, itemId, marketplaceSource)
				.orElseThrow(() -> new NotificationItemNotFoundException("Notification item not found"));
		item.setNotificationsEnabled(isEnabled);
		notificationItemRepository.save(item);
		return ToggleNotificationsResponse.builder()
				.email(email)
				.itemId(itemId)
				.marketplaceSource(marketplaceSource)
				.isEnabled(isEnabled)
				.build();
	}
}
