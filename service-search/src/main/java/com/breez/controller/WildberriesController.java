package com.breez.controller;

import com.breez.exception.EmptyResponseException;
import com.breez.exception.InvalidParametersException;
import com.breez.exception.ServerException;
import com.breez.model.Response;
import com.breez.service.CommonService;
import com.breez.service.ResponseService;
import com.breez.service.WildberriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.breez.constants.Constants.COMMON_PAGE;
import static com.breez.constants.Constants.COMMON_SORT_POPULAR;

@RestController
@RequiredArgsConstructor
@RequestMapping("/service-search/api")
public class WildberriesController {

	private final CommonService commonService;
	private final WildberriesService wildberriesService;

	@GetMapping(value = "/wildberries", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response> makeRequest(
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "sort", required = false, defaultValue = COMMON_SORT_POPULAR) String sort,
			@RequestParam(value = "page", required = false, defaultValue = COMMON_PAGE) String page) {
		try {
			commonService.validateInputParameters(title, sort, page);

			Map<String, String> parameters = wildberriesService.getSearchParameters(title, sort, page);
			List<Map<String, Object>> response = wildberriesService.makeRequest(parameters);
			if (response == null || response.isEmpty()) {
				throw new EmptyResponseException(HttpStatus.NOT_FOUND, "Wildberries: No products found");
			}
			return ResponseService.successResponse(Map.of("products", response));
		} catch (IOException e) {
			throw new ServerException(HttpStatus.BAD_GATEWAY, "Wildberries: Failed to fetch data from source");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ServerException(HttpStatus.SERVICE_UNAVAILABLE, "Wildberries: Interrupted while processing request");
		}
	}

	@GetMapping(value = "/wildberries/product", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response> makeRequestProduct(@RequestParam(value = "id", required = false) String paramId) {
		long id;
		try {
			id = Long.parseLong(paramId);
			Map<String, Object> info = wildberriesService.makeRequestProduct(id);
			if (info == null || info.isEmpty()) {
				throw new EmptyResponseException(HttpStatus.NOT_FOUND, String.format("Ozon: No info for product with id=%d found", id));
			}
			return ResponseService.successResponse(info);
		} catch (Exception e) {
			throw new InvalidParametersException(HttpStatus.BAD_REQUEST, "Invalid parameter: id");
		}
	}

}
