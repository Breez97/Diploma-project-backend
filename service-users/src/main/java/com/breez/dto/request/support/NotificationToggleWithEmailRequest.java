package com.breez.dto.request.support;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationToggleWithEmailRequest {

	private String email;
	private Long itemId;
	private String marketplaceSource;
	private String notification;

}
