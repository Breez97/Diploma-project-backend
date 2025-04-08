package com.breez.controller;

import com.breez.exception.EmptyResponseException;
import com.breez.exception.InvalidParametersException;
import com.breez.exception.ServerException;
import com.breez.model.Response;
import com.breez.service.OzonService;
import com.breez.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/service-search/api")
public class OzonController {

	private final OzonService ozonService;

	@GetMapping(value = "/ozon", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response> makeRequest(@RequestParam(value = "title", required = false) String title) {
		try {
			if (title != null) {
				title = URLEncoder.encode(title, StandardCharsets.UTF_8);
			}

			List<Map<String, Object>> response = ozonService.makeRequest(title);
			if (response == null || response.isEmpty()) {
				throw new EmptyResponseException(HttpStatus.NOT_FOUND, "Ozon: No products found");
			}

			return ResponseService.successResponse(Map.of("products", response));
		} catch (IOException e) {
			throw new ServerException(HttpStatus.BAD_GATEWAY, "Ozon: Failed to fetch data from source");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ServerException(HttpStatus.SERVICE_UNAVAILABLE, "Ozon: Interrupted while processing request");
		}
	}

	@GetMapping(value = "/ozon/product", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response> makeRequestProduct(@RequestParam(value = "id", required = false) String paramId) {
		long id;
		try {
			id = Long.parseLong(paramId);
		} catch (Exception e) {
			throw new InvalidParametersException(HttpStatus.BAD_REQUEST, "Invalid parameter: id");
		}
		return null;
	}

}