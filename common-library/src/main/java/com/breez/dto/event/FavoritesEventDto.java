package com.breez.dto.event;

import com.breez.enums.ActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoritesEventDto {

	private String email;
	private Long itemId;
	private String marketplaceSource;
	private ActionType action;

}
