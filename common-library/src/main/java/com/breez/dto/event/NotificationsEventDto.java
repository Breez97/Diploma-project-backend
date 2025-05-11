package com.breez.dto.event;

import com.breez.enums.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationsEventDto {

	private String email;
	private Long itemId;
	private String marketplaceSource;
	private Notification notification;

}
