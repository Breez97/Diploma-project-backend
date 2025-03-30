package com.breez.controller;

import com.breez.exception.EmptyResponseException;
import com.breez.exception.InvalidParametersException;
import com.breez.model.Response;
import com.breez.service.ResponseService;
import com.breez.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
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

	private final SearchService searchService;

	@GetMapping("/wildberries")
	public Mono<ResponseEntity<Response>> wildberriesFetchData(@RequestParam(value = "title", required = false) String title) {
		if (StringUtils.isBlank(title)) {
			return Mono.error(new InvalidParametersException(HttpStatus.BAD_REQUEST, "Invalid parameter: title"));
		}
		String formattedTitle = title.replace(" ", "+");
		return searchService.wildberriesFetchData(formattedTitle)
				.switchIfEmpty(Mono.error(new EmptyResponseException(HttpStatus.NOT_FOUND, "Wildberries: No products found")))
				.map(products -> {
					Map<String, Object> data = Map.of("products", products);
					return ResponseService.successResponse(data);
				});
	}

	@GetMapping("/wildberries/product")
	public Mono<ResponseEntity<Response>> wildberriesProductFetchData(@RequestParam(value = "id") Long id) {
		if (id == null) {
			return Mono.error(new InvalidParametersException(HttpStatus.BAD_REQUEST, "Invalid parameter: title"));
		}
		return null;
	}

}
