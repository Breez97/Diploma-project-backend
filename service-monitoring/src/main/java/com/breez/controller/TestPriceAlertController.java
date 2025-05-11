package com.breez.controller;

import com.breez.dto.Response;
import com.breez.service.TestPriceAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/send/alert")
@RequiredArgsConstructor
public class TestPriceAlertController {

	private final TestPriceAlertService testPriceAlertService;

	@GetMapping
	public ResponseEntity<Response<Void>> sendTestPriceAlert() {
		testPriceAlertService.sendTestPriceAlertEvent();
		return ResponseEntity.ok(Response.success("sent"));
	}

}
