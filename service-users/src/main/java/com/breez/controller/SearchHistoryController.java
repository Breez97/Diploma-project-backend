package com.breez.controller;

import com.breez.dto.Response;
import com.breez.dto.request.AddSearchHistoryRequest;
import com.breez.dto.response.SearchHistoryResponse;
import com.breez.security.CustomUserDetails;
import com.breez.service.SearchHistoryService;
import com.breez.service.ValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/history")
@RequiredArgsConstructor
public class SearchHistoryController {

	private static final Logger logger = LoggerFactory.getLogger(SearchHistoryController.class);

	private final SearchHistoryService searchHistoryService;
	private final ValidationService validationService;

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Response<List<SearchHistoryResponse>>> getSearchHistory(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		validationService.validateHeaders(sessionId, logger);
		List<SearchHistoryResponse> response = searchHistoryService.getSearchHistory(userDetails.getId());
		return ResponseEntity.ok(Response.success(response, "Search history retrieved successfully"));
	}

	@PostMapping("/add")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Response<SearchHistoryResponse>> addSearchHistory(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@Valid @RequestBody AddSearchHistoryRequest request) {
		validationService.validateHeaders(sessionId, logger);
		SearchHistoryResponse response = searchHistoryService.addSearchHistory(userDetails.getId(), request);
		return ResponseEntity.ok(Response.success(response, "Search value added successfully"));
	}

	@GetMapping("/clear")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Response<Void>> clearSearchHistory(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		validationService.validateHeaders(sessionId, logger);
		searchHistoryService.clearSearchHistory(userDetails.getId());
		return ResponseEntity.ok(Response.success("Search history was cleared"));
	}

}
