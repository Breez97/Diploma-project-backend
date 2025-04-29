package com.breez.controller;

import com.breez.exception.EmptyResponseException;
import com.breez.exception.InvalidParametersException;
import com.breez.exception.ServerException;
import com.breez.model.ProductChunkResult;
import com.breez.model.Response;
import com.breez.service.*;
import com.breez.service.marketplace.OzonService;
import com.breez.service.ProductsFetchingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.breez.constants.Constants.*;

@RestController
@RequestMapping("/api/v1")
public class OzonController {

	private static final Logger logger = LoggerFactory.getLogger(OzonController.class);

	private final OzonService ozonService;
	private final ProductsFetchingService productsFetchingService;
	private final ValidationService validationService;

	@Autowired
	public OzonController(OzonService ozonService, ProductsFetchingService productsFetchingService, ValidationService validationService) {
		this.ozonService = ozonService;
		this.productsFetchingService = productsFetchingService;
		this.validationService = validationService;
	}

	@ApiResponses({
			@ApiResponse(responseCode = "200",
					content = @Content(schema = @Schema(implementation = Response.class),
							examples = @ExampleObject(
									name = "Успешный ответ",
									value = "{\"timestamp\":\"2025-04-13T16:13:46.019908557\",\"status\":\"success\",\"data\":{\"products\":[{\"id\":1847032696,\"externalLink\":\"https://www.ozon.ru/product/1847032696\",\"title\":\"Кроссовки Turino II\",\"imageUrl\":\"https://cdn1.ozone.ru/s3/multimedia-1-a/7297651402.jpg\",\"brand\":\"PUMA\",\"price\":\"4358\",\"rating\":\"4.8\",\"feedbacks\":\"26\"}]}}\n"
							))),
			@ApiResponse(content = @Content(schema = @Schema(implementation = Response.class),
							examples = @ExampleObject(
									name = "Ошибка с описанием",
									value = "{\"timestamp\":\"2025-04-13T16:19:47.969320249\",\"status\":\"error\",\"data\":{\"message\":\"Ozon: error_message\"}}"
							))),
	})
	@Tag(name = "OZON")
	@Operation(summary = "Получение информации о товарах Ozon")
	@GetMapping(value = "/ozon", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response> makeRequest(
			@Parameter(description = "Сессия пользователя")
			@RequestHeader(value = "Session-Id", required = false) String sessionId,

			@Parameter(description = "Название товара для поиска", required = true)
			@RequestParam(value = "title", required = false) String title,

			@Parameter(description = "Параметр для сортировки результатов поиска", schema = @Schema(allowableValues = {"popular", "new", "priceasc", "pricedesc", "rating"}, defaultValue = "popular"))
			@RequestParam(value = "sort", required = false, defaultValue = DEFAULT_SORT) String sort,

			@Parameter(description = "Индекс запрашиваемой порции", required = true, schema = @Schema(defaultValue = "0"))
			@RequestParam(value = "chunk") String chunk)
	{
		validationService.validateHeaders(sessionId, logger);
		validationService.validateInputParameters(title, sort, chunk, logger);

		int chunkIndex = Integer.parseInt(chunk);
		ProductChunkResult result = productsFetchingService.getProductChunk(ozonService, sessionId, title, sort, chunkIndex);

		if (result.getProducts().isEmpty()) {
			return ResponseService.errorResponse(HttpStatus.NOT_FOUND, Map.of("message", "No products found"));
		}

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("products", result.getProducts());
		responseData.put("hasMore", result.hasMore());
		logger.info("Session [{}], Market [{}], Search [{}], Chunk [{}]: Returning {} products, hasMore: {}", sessionId, OZON, title, chunkIndex, result.getProducts().size(), result.hasMore());
		return ResponseService.successResponse(responseData);
	}

	@ApiResponses({
			@ApiResponse(responseCode = "200",
					content = @Content(schema = @Schema(implementation = Response.class),
							examples = @ExampleObject(
									name = "Успешный ответ",
									value = "{\"timestamp\":\"2025-04-13T16:25:56.875971742\",\"status\":\"success\",\"data\":{\"id\":1847032696,\"externalLink\":\"https://www.ozon.ru/product/1847032696\",\"title\":\"Кроссовки PUMA Turino II\",\"imageUrl\":\"https://cdn1.ozone.ru/s3/multimedia-1-a/7297651402.jpg\",\"brand\":\"PUMA\",\"price\":\"4358\",\"rating\":\"4.8\",\"feedbacks\":\"26\",\"description\":\"Создайте стильный ретро-образ для прогулки по городу. Эти кроссовки, выполненные в стиле футбольных бутс, обеспечивают превосходную амортизацию и комфорт в течение всего дня. А благодаря логотипу PUMA и подошве с геометрическим рисунком вы точно привлечете внимание окружающих. Наденьте кроссовки Turino II, зашнуруйте и приготовьтесь забивать голы.\",\"options\":[{\"name\":\"Материал\",\"value\":\"Синтетика\"},{\"name\":\"Материал стельки\",\"value\":\"Текстиль\"},{\"name\":\"Материал подошвы обуви\",\"value\":\"Резина\"},{\"name\":\"Сезон\",\"value\":\"На любой сезон\"},{\"name\":\"Бренд в одежде и обуви\",\"value\":\"PUMA\"}]}}"
							))),
			@ApiResponse(content = @Content(schema = @Schema(implementation = Response.class),
							examples = @ExampleObject(
									name = "Ошибка с описанием",
									value = "{\"timestamp\":\"2025-04-13T16:19:47.969320249\",\"status\":\"error\",\"data\":{\"message\":\"Ozon: error_message\"}}"
							))),
	})
	@Tag(name = "OZON")
	@Operation(summary = "Получение информации о товаре Ozon по id")
	@GetMapping(value = "/ozon/product", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response> makeRequestProduct(
			@Parameter(description = "id товара для получения информации", required = true)
			@RequestParam(value = "id", required = false) String paramId)
	{
		long id;
		try {
			id = Long.parseLong(paramId);
			Map<String, Object> info = ozonService.fetchSingleProduct(id);
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