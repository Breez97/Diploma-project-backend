package com.breez.dto.request;

import lombok.Data;

@Data
public class NotificationInfoRequest {

	private String email;
	private Long itemId;
	private String marketplaceSource;

}
