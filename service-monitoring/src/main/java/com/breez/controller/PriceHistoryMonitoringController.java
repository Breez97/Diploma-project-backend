package com.breez.controller;

import com.breez.dto.Response;
import com.breez.dto.request.PriceRequest;
import com.breez.dto.response.PriceHistoryResponse;
import com.breez.service.PriceHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/monitoring")
@RequiredArgsConstructor
public class PriceHistoryMonitoringController {

	private final PriceHistoryService priceHistoryService;

	@GetMapping("/item/history")
	public ResponseEntity<Response<PriceHistoryResponse>> getProductPriceHistory(@Valid PriceRequest request) {
		PriceHistoryResponse response = priceHistoryService.getPriceHistoryForMonitoredItem(request);
		return ResponseEntity.ok(Response.success(response, "Item price history found"));
	}

}
