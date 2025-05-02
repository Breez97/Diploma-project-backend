package com.breez.controller;

import com.breez.dto.Response;
import com.breez.dto.request.CombinedSearchRequest;
import com.breez.dto.response.ProductsSearchResponse;
import com.breez.exception.NoProductsFoundException;
import com.breez.model.ProductChunkResult;
import com.breez.service.CombinedProductFetchingService;
import com.breez.service.ValidationService;
import com.breez.service.marketplace.MarketplaceService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class CombinedController {

	private static final Logger logger = LoggerFactory.getLogger(CombinedController.class);

	private final CombinedProductFetchingService combinedProductFetchingService;
	private final ValidationService validationService;
	private final Map<String, MarketplaceService> availableMarketplaceServices;

	@Autowired
	public CombinedController(
			CombinedProductFetchingService combinedProductFetchingService,
			ValidationService validationService,
			List<MarketplaceService> allMarketplaceServices) {
		this.combinedProductFetchingService = combinedProductFetchingService;
		this.validationService = validationService;
		this.availableMarketplaceServices = allMarketplaceServices.stream()
				.filter(Objects::nonNull)
				.filter(s -> s.getMarketplaceIdentifier() != null && !s.getMarketplaceIdentifier().isBlank())
				.collect(Collectors.toMap(
						MarketplaceService::getMarketplaceIdentifier,
						Function.identity(),
						(service1, service2) -> service1
				));
	}

	@GetMapping(value = "/combined", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<ProductsSearchResponse>> fetchProductsCombined(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@Valid CombinedSearchRequest request) {
		validationService.validateHeaders(sessionId, logger);

		String formattedMarketplaces = request.getMarketplaces().toLowerCase();
		String[] marketplacesArray = formattedMarketplaces.split(",");
		List<String> validMarketplaces = new ArrayList<>(Arrays.asList(marketplacesArray));

		List<MarketplaceService> selectedServices = validMarketplaces.stream()
				.map(availableMarketplaceServices::get)
				.filter(Objects::nonNull)
				.toList();

		ProductChunkResult result = combinedProductFetchingService.getCombinedProductChunk(sessionId, request.getTitle(), request.getSort(), request.getChunk(), selectedServices);
		ProductsSearchResponse response = new ProductsSearchResponse(result.hasMore(), result.getProducts());
		return ResponseEntity.ok(Response.success(response, "Combined: products found successfully"));
	}

}
