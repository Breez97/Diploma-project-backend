package com.breez.controller;

import com.breez.dto.Response;
import com.breez.service.TestPriceAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/send-test-alert")
@RequiredArgsConstructor
@Tag(name = "Test Price Alert Controller")
public class TestPriceAlertController {

	private final TestPriceAlertService testPriceAlertService;

	@Operation(summary = "Тестовый эндпоинт для отправки email-уведомления с замоканными данными")
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<Void>> sendTestPriceAlert() {
		testPriceAlertService.sendTestPriceAlertEvent();
		return ResponseEntity.ok(Response.success("sent"));
	}

}
