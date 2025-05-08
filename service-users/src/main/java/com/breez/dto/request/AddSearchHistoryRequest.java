package com.breez.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddSearchHistoryRequest {

	@NotNull(message = "Search value can't be empty")
	private String searchHistory;

}
