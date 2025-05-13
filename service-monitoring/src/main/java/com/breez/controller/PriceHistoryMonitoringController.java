package com.breez.controller;

import com.breez.dto.Response;
import com.breez.dto.request.PriceRequest;
import com.breez.dto.response.PriceHistoryResponse;
import com.breez.service.PriceHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/monitoring")
@RequiredArgsConstructor
public class PriceHistoryMonitoringController {

	private final PriceHistoryService priceHistoryService;

	@Operation(summary = "Внутренний эндпоинт для получения истории цены на определенный товар из избранного пользователя")
	@GetMapping(value = "/item/history", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<PriceHistoryResponse>> getProductPriceHistory(@Valid PriceRequest request) {
		PriceHistoryResponse response = priceHistoryService.getPriceHistoryForMonitoredItem(request);
		return ResponseEntity.ok(Response.success(response, "Item price history found"));
	}

}
