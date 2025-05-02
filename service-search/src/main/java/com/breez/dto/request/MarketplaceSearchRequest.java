package com.breez.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.breez.constants.Constants.DEFAULT_CHUNK;
import static com.breez.constants.Constants.DEFAULT_SORT;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketplaceSearchRequest {

	private String title;

	private String sort = DEFAULT_SORT;

	private Integer chunk = DEFAULT_CHUNK;

}
