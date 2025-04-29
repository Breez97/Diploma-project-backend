package com.breez.controller;

import com.breez.model.ProductChunkResult;
import com.breez.model.Response;
import com.breez.service.CombinedProductFetchingService;
import com.breez.service.ResponseService;
import com.breez.service.ValidationService;
import com.breez.service.marketplace.MarketplaceService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.breez.constants.Constants.DEFAULT_SORT;
import static com.breez.constants.Constants.OZON;

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
	public ResponseEntity<Response> makeRequest(
			@Parameter(description = "Сессия пользователя")
			@RequestHeader(value = "Session-Id", required = false) String sessionId,

			@Parameter(description = "Название товара для поиска", required = true)
			@RequestParam(value = "title", required = false) String title,

			@Parameter(description = "Параметр для сортировки результатов поиска", schema = @Schema(allowableValues = {"popular", "new", "priceasc", "pricedesc", "rating"}, defaultValue = "popular"))
			@RequestParam(value = "sort", required = false, defaultValue = DEFAULT_SORT) String sort,

			@Parameter(description = "Индекс запрашиваемой порции", required = true, schema = @Schema(defaultValue = "0"))
			@RequestParam(value = "chunk") String chunk,

			@Parameter(description = "Список маркетплейсов", required = true)
			@RequestParam(value = "marketplaces") String marketplaces)
	{
		validationService.validateHeaders(sessionId, logger);
		validationService.validateInputParameters(title, sort, chunk, logger);
		validationService.validateMarketplaces(marketplaces, logger);

		String formattedMarketplaces = marketplaces.toLowerCase();
		String[] marketplacesArray = formattedMarketplaces.split(",");
		List<String> validMarketplaces = new ArrayList<>(Arrays.asList(marketplacesArray));

		List<MarketplaceService> selectedServices = validMarketplaces.stream()
				.map(availableMarketplaceServices::get)
				.filter(Objects::nonNull)
				.toList();

		int chunkIndex = Integer.parseInt(chunk);
		ProductChunkResult result = combinedProductFetchingService.getCombinedProductChunk(sessionId, title, sort, chunkIndex, selectedServices);

		if (result.getProducts().isEmpty()) {
			return ResponseService.errorResponse(HttpStatus.NOT_FOUND, Map.of("message", "No products found"));
		}
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("products", result.getProducts());
		responseData.put("hasMore", result.hasMore());
		return ResponseService.successResponse(responseData);
	}

}
