package com.breez.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.breez.constants.Constants.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CombinedSearchRequest {

	private String title;

	private String sort = DEFAULT_SORT;

	private Integer chunk = DEFAULT_CHUNK;

	private String marketplaces = DEFAULT_MARKETPLACES;

}
