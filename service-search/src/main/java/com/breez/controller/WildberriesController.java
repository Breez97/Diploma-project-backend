package com.breez.controller;

import com.breez.exception.EmptyResponseException;
import com.breez.exception.InvalidParametersException;
import com.breez.exception.ServerException;
import com.breez.model.ProductChunkResult;
import com.breez.model.Response;
import com.breez.service.*;
import com.breez.service.ProductsFetchingService;
import com.breez.service.marketplace.WildberriesService;
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
import java.util.*;

import static com.breez.constants.Constants.*;

@RestController
@RequestMapping("/api/v1")
public class WildberriesController {

	private static final Logger logger = LoggerFactory.getLogger(WildberriesController.class);

	private final ProductsFetchingService productsFetchingService;
	private final ValidationService validationService;
	private final WildberriesService wildberriesService;

	@Autowired
	public WildberriesController(
			ProductsFetchingService productsFetchingService,
			ValidationService validationService,
			WildberriesService wildberriesService) {
		this.productsFetchingService = productsFetchingService;
		this.validationService = validationService;
		this.wildberriesService = wildberriesService;
	}

	@ApiResponses({
			@ApiResponse(responseCode = "200",
					content = @Content(schema = @Schema(implementation = Response.class),
							examples = @ExampleObject(
									name = "Успешный ответ",
									value = "{\"timestamp\":\"2025-04-13T16:56:03.039074757\",\"status\":\"success\",\"data\": {\"products\": [ {\"id\": 139654489,\"externalLink\":\"https://www.wildberries.ru/catalog/139654489/detail.aspx\",\"title\":\"Платье праздничное\",\"imageUrl\":\"https://basket-10.wbbasket.ru/vol1396/part139654/139654489/images/big/1.webp\",\"brand\":\"Michaelangelo\",\"price\":\"17271\",\"rating\":\"4.9\",\"feedbacks\":\"516\" } ] } }"
							))),
			@ApiResponse(content = @Content(schema = @Schema(implementation = Response.class),
					examples = @ExampleObject(
							name = "Ошибка с описанием",
							value = "{\"timestamp\":\"2025-04-13T16:19:47.969320249\",\"status\":\"error\",\"data\":{\"message\":\"Wildberries: error_message\"}}"
					))),
	})
	@Tag(name = "WILDBERRIES")
	@Operation(summary = "Получение информации о товарах Wildberries")
	@GetMapping(value = "/wildberries", produces = MediaType.APPLICATION_JSON_VALUE)
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
		ProductChunkResult result = productsFetchingService.getProductChunk(wildberriesService, sessionId, title, sort, chunkIndex);

		if (result.getProducts().isEmpty()) {
			return ResponseService.errorResponse(HttpStatus.NOT_FOUND, Map.of("message", "No products found"));
		}

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("products", result.getProducts());
		responseData.put("hasMore", result.hasMore());
		logger.info("Session [{}], Market [{}], Search [{}], Chunk [{}]: Returning {} products, hasMore: {}", sessionId, WILDBERRIES, title, chunkIndex, result.getProducts().size(), result.hasMore());
		return ResponseService.successResponse(responseData);
	}

	@ApiResponses({
			@ApiResponse(responseCode = "200",
					content = @Content(schema = @Schema(implementation = Response.class),
							examples = @ExampleObject(
									name = "Успешный ответ",
									value = "{\"timestamp\":\"2025-04-13T15:37:49.261368464\",\"status\":\"success\",\"data\": {\"id\": 340797177,\"externalLink\":\"https://www.wildberries.ru/catalog/340797177/detail.aspx\",\"title\":\"Платье лапша на бретельках летнее длинное\",\"imageUrl\":\"https://basket-20.wbbasket.ru/vol3407/part340797/340797177/images/big/1.webp\",\"brand\":\"Dizori\",\"price\":\"1837\",\"rating\":\"4.7\",\"feedbacks\":\"3637\",\"description\":\"Базовое летнее повседневное платье лапша с вырезом на бретельках «Афина». Праздничное вечернее облегающее длинное женское платье майка для любого случая! Платье сарафан с открытыми плечами выполнено из качественной эластичной ткани - лапша в рубчик. Платье комбинация длины миди макси не просвечивает, не деформируется, не мнется. Весеннее платье майка силуэтное – прекрасный выбор, благодаря эластичности подходит на любой тип фигуры. Тренд 2025 года, платье подойдет молодым девушкам и женщинам любого возраста. Наше платье с открытой спиной в обтяг дополнит романтичный образ на берегу моря в отпуске. В стиле Ким Кардашьян скимс (skims). Прямое удлиненное на бретелях в обтяжку красиво облегает изгибы тела. Пусть оно обтягивающее, но при этом комфортное и эластичное, а значит подойдет как для вечернего праздничного обрза, а так же как домашнее. Оно легкое и на тонких бретелях, а значит подойдет на каждый день. а ещё на день влюбленных. Создавай идеальный образ на праздник, на свидание, на фотосессию и для дома.\",\"options\": [ {\"name\":\"Состав\",\"value\":\"5% спандекс, 95% полиэстер\" }, {\"name\":\"Цвет\",\"value\":\"бордовый; красновато-бордовый\" }, {\"name\":\"Пол\",\"value\":\"Женский\" }, {\"name\":\"Размер на модели\",\"value\":\"M\" } ] } }"
							))),
			@ApiResponse(content = @Content(schema = @Schema(implementation = Response.class),
					examples = @ExampleObject(
							name = "Ошибка с описанием",
							value = "{\"timestamp\":\"2025-04-13T16:19:47.969320249\",\"status\":\"error\",\"data\":{\"message\":\"Wildberries: error_message\"}}"
					))),
	})
	@Tag(name = "WILDBERRIES")
	@Operation(summary = "Получение информации о товаре Wildberries по id")
	@GetMapping(value = "/wildberries/product", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response> makeRequestProduct(
			@Parameter(description = "Id товара для получения информации", required = true)
			@RequestParam(value = "id", required = false) String paramId)
	{
		long id;
		try {
			id = Long.parseLong(paramId);
			Map<String, Object> info = wildberriesService.fetchSingleProduct(id);
			if (info == null || info.isEmpty()) {
				throw new EmptyResponseException(HttpStatus.NOT_FOUND, String.format("Wildberries: No info for product with id=%d found", id));
			}
			return ResponseService.successResponse(info);
		} catch (NumberFormatException e) {
			throw new InvalidParametersException(HttpStatus.BAD_REQUEST, "Wildberries: Invalid parameter: id");
		} catch (IOException e) {
			throw new ServerException(HttpStatus.BAD_GATEWAY, "Wildberries: Failed to fetch data from source");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ServerException(HttpStatus.SERVICE_UNAVAILABLE, "Wildberries: Interrupted while processing request");
		}
	}

}
