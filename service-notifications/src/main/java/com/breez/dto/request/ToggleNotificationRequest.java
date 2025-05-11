package com.breez.dto.request;

import lombok.Data;

@Data
public class ToggleNotificationRequest {

	private String email;
	private Long itemId;
	private String marketplaceSource;
	private String notification;

}
