package com.breez.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoritesItemResponse {

	private Long itemId;
	private String marketplaceSource;
	private LocalDateTime addedAt;

}
