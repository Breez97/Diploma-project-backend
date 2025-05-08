package com.breez.service;

import com.breez.dto.request.AddSearchHistoryRequest;
import com.breez.dto.response.SearchHistoryResponse;

import java.util.List;

public interface SearchHistoryService {

	List<SearchHistoryResponse> getSearchHistory(Long userId);

	SearchHistoryResponse addSearchHistory(Long userId, AddSearchHistoryRequest request);

	void clearSearchHistory(Long userId);

}
