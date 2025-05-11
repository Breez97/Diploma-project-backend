package com.breez.controller;

import com.breez.dto.Response;
import com.breez.dto.ProductDetailsDto;
import com.breez.dto.request.MarketplaceSearchRequest;
import com.breez.dto.request.SingleProductSearchRequest;
import com.breez.dto.response.ProductsSearchResponse;
import com.breez.model.ProductChunkResult;
import com.breez.service.ProductsFetchingService;
import com.breez.service.ValidationService;
import com.breez.service.marketplace.OzonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class OzonController {

	private static final Logger logger = LoggerFactory.getLogger(OzonController.class);

	private final OzonService ozonService;
	private final ProductsFetchingService productsFetchingService;
	private final ValidationService validationService;

	@GetMapping(value = "/ozon", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<ProductsSearchResponse>> fetchProductsOzon(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@Valid MarketplaceSearchRequest request) {
		validationService.validateHeaders(sessionId, logger);
		ProductChunkResult result = productsFetchingService.getProductChunk(ozonService, sessionId, request.getTitle(), request.getSort(), request.getChunk());
		ProductsSearchResponse response = new ProductsSearchResponse(result.hasMore(), result.getProducts());
		return ResponseEntity.ok(Response.success(response, "Ozon: products found successfully"));
	}

	@GetMapping(value = "/ozon/product", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<ProductDetailsDto>> fetchSingleProductOzon(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@Valid SingleProductSearchRequest request) {
		validationService.validateHeaders(sessionId, logger);
		ProductDetailsDto result = ozonService.fetchSingleProduct(request.getId());
		return ResponseEntity.ok(Response.success(result, "Ozon: single product info found successfully"));
	}

}