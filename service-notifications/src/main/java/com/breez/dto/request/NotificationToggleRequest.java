package com.breez.dto.request;

import lombok.Data;

@Data
public class NotificationToggleRequest {

	private String email;
	private Long itemId;
	private String marketplaceSource;
	private String notification;

}
