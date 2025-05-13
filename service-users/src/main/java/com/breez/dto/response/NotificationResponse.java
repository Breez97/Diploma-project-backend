package com.breez.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

	private Long itemId;
	private String marketplaceSource;
	private Boolean isEnabled;

}
