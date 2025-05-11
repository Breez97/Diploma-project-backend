package com.breez.controller;

import com.breez.dto.Response;
import com.breez.dto.request.PriceRequest;
import com.breez.dto.response.PriceHistoryResponse;
import com.breez.security.CustomUserDetails;
import com.breez.service.PriceService;
import com.breez.service.ValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/price")
@RequiredArgsConstructor
public class PriceController {

	private static final Logger logger = LoggerFactory.getLogger(PriceController.class);

	private final ValidationService validationService;
	private final PriceService priceService;

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Response<PriceHistoryResponse>> getPriceHistory(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@Valid PriceRequest request) {
		validationService.validateHeaders(sessionId, logger);
		PriceHistoryResponse response = priceService.getPriceHistory(userDetails.getUsername(), request);
		return ResponseEntity.ok(Response.success(response, "Price history fetched"));
	}

}
