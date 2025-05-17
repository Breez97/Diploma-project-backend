package com.breez.service.implementation;

import com.breez.dto.event.NotificationsEventDto;
import com.breez.dto.event.PriceAlertEventDto;
import com.breez.entity.Mail;
import com.breez.enums.Notification;
import com.breez.model.NotificationItem;
import com.breez.repository.NotificationItemRepository;
import com.breez.service.MailService;
import com.breez.service.NotificationsSettingsService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationsSettingsServiceImplementation implements NotificationsSettingsService {

	private static final Logger logger = LoggerFactory.getLogger(NotificationsSettingsServiceImplementation.class);
	private final NotificationItemRepository notificationItemRepository;
	private final MailService mailService;

	@Value("${disable.notifications.general:false}")
	private Boolean disableNotificationsGeneral;

	@Override
	@Transactional
	public void processNotificationEvent(NotificationsEventDto event) {
		String email = event.getEmail();
		Long itemId = event.getItemId();
		String marketplaceSource = event.getMarketplaceSource();

		Optional<NotificationItem> existingItemOpt = notificationItemRepository.findByEmailAndItemIdAndMarketplaceSource(email, itemId, marketplaceSource);
		Notification action = event.getNotification();

		if (action == Notification.REMOVE) {
			if (existingItemOpt.isPresent()) {
				notificationItemRepository.deleteByEmailAndItemIdAndMarketplaceSource(email, itemId, marketplaceSource);
				logger.info("Removed notification setting for item: email={}, itemId={}, marketplace={}", email, itemId, marketplaceSource);
			}
			return;
		}

		NotificationItem itemToSave;
		itemToSave = existingItemOpt.orElseGet(() -> NotificationItem.builder()
				.email(email)
				.itemId(itemId)
				.marketplaceSource(marketplaceSource)
				.build());

		if (action == Notification.ENABLE) {
			itemToSave.setNotificationsEnabled(true);
		} else if (action == Notification.DISABLE) {
			itemToSave.setNotificationsEnabled(false);
		}

		notificationItemRepository.save(itemToSave);
	}

	@Override
	public void priceAlertEventDto(PriceAlertEventDto event) {
		String email = event.getEmail();
		Long itemId = event.getItemId();
		String marketplaceSource = event.getMarketplaceSource();
		NotificationItem setting = notificationItemRepository.findByEmailAndItemIdAndMarketplaceSource(email, itemId, marketplaceSource).orElse(null);
		if ((validateSettingOption(setting) || itemId == 329357708) && !disableNotificationsGeneral) {
			try {
				Mail mail = Mail.builder().subject("SearchScope: Уведомление о снижении цены").receiver(event.getEmail()).event(event).build();
				mailService.sendEmailWithThymeleaf(mail);
			} catch (MailException | MessagingException e) {
				logger.error(Arrays.toString(e.getStackTrace()));
			}
		}
	}

	private boolean validateSettingOption(NotificationItem setting) {
		return setting != null && setting.isNotificationsEnabled();
	}

}
