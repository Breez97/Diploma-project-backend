package com.breez.controller;

import com.breez.exception.EmptyResponseException;
import com.breez.exception.InvalidParametersException;
import com.breez.exception.ServerException;
import com.breez.model.Response;
import com.breez.service.CommonService;
import com.breez.service.OzonService;
import com.breez.service.ResponseService;
import org.springframework.beans.factory.annotation.Autowired;
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

import static com.breez.constants.Constants.*;

@RestController
@RequestMapping("/api/v1")
public class OzonController {

	private final CommonService commonService;
	private final OzonService ozonService;

	@Autowired
	public OzonController(CommonService commonService, OzonService ozonService) {
		this.commonService = commonService;
		this.ozonService = ozonService;
	}

	@GetMapping(value = "/ozon", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response> makeRequest(
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "sort", required = false, defaultValue = COMMON_SORT_POPULAR) String sort,
			@RequestParam(value = "page", required = false, defaultValue = COMMON_PAGE) String page) {
		try {
			commonService.validateInputParameters(OZON, title, sort, page);

			Map<String, String> parameters = ozonService.getSearchParameters(title, sort, page);
			List<Map<String, Object>> response = ozonService.makeRequest(parameters);
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
			Map<String, Object> info = ozonService.makeRequestProduct(id);
			if (info == null || info.isEmpty()) {
				throw new EmptyResponseException(HttpStatus.NOT_FOUND, String.format("Ozon: No info for product with id=%d found", id));
			}
			return ResponseService.successResponse(info);
		} catch (NumberFormatException e) {
			throw new InvalidParametersException(HttpStatus.BAD_REQUEST, "Ozon: Invalid parameter: id");
		} catch (IOException e) {
			throw new ServerException(HttpStatus.BAD_GATEWAY, "Ozon: Failed to fetch data from source");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ServerException(HttpStatus.SERVICE_UNAVAILABLE, "Ozon: Interrupted while processing request");
		}
	}

}