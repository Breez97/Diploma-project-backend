package com.breez.controller;

import com.breez.exception.EmptyResponseException;
import com.breez.exception.InvalidParametersException;
import com.breez.model.Response;
import com.breez.service.ResponseService;
import com.breez.service.WildberriesService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/service-search/api")
public class WildberriesController {

	private final WildberriesService wildberriesService;

	@GetMapping(value = "/wildberries", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<Response>> fetchData(@RequestParam(value = "title", required = false) String title) {
		if (StringUtils.isBlank(title)) {
			return Mono.error(new InvalidParametersException(HttpStatus.BAD_REQUEST, "Invalid parameter: title"));
		}
		String formattedTitle = title.replace(" ", "+");
		return wildberriesService.fetchData(formattedTitle)
				.switchIfEmpty(Mono.error(new EmptyResponseException(HttpStatus.NOT_FOUND, "Wildberries: No products found")))
				.map(products -> {
					Map<String, Object> data = Map.of("products", products);
					return ResponseService.successResponse(data);
				});
	}

	@GetMapping(value = "/wildberries/product", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<Response>> fetchDataProduct(@RequestParam(value = "id", required = false) String paramId) {
		long id;
		try {
			id = Long.parseLong(paramId);
		} catch (Exception e) {
			return Mono.error(new InvalidParametersException(HttpStatus.BAD_REQUEST, "Invalid parameter: id"));
		}
		return wildberriesService.fetchDataProduct(id)
				.switchIfEmpty(Mono.error(new EmptyResponseException(HttpStatus.NOT_FOUND, String.format("Wildberries: No info for product with id=%d found", id))))
				.map(ResponseService::successResponse);
	}

}
