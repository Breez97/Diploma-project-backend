package com.breez.dto.request.support;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationInfoWithEmailRequest {

	private String email;
	private Long itemId;
	private String marketplaceSource;

}
