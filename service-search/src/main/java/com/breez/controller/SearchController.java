package com.breez.controller;

import com.breez.model.Response;
import com.breez.service.ResponseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/service-search/api/v1")
public class SearchController {

	@GetMapping("/search")
	public ResponseEntity<Response> getMarket(@RequestParam("market") String name) {
		Map<String, Object> data = Map.of("title", name);
		return ResponseService.successResponse(data);
	}

}
