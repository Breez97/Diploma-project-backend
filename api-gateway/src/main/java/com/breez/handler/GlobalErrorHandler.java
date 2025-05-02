package com.breez.handler;

import com.breez.dto.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@Order(-1)
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

	private final ObjectMapper objectMapper;

	public GlobalErrorHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	@NonNull
	public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {
		ServerHttpResponse response = exchange.getResponse();

		if (response.isCommitted()) {
			return Mono.error(ex);
		}

		HttpStatus status = determineHttpStatus(ex);
		String message = buildErrorMessage(ex, exchange);
		response.setStatusCode(status);
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

		Response customResponse = new Response();

		try {
			byte[] responseBytes = objectMapper.writeValueAsBytes(customResponse);
			DataBufferFactory bufferFactory = response.bufferFactory();
			DataBuffer dataBuffer = bufferFactory.wrap(responseBytes);
			return response.writeWith(Mono.just(dataBuffer));
		} catch (JsonProcessingException e) {
			return Mono.error(e);
		}
	}

	private HttpStatus determineHttpStatus(Throwable ex) {
		if (ex instanceof ResponseStatusException rse) {
			return convertToHttpStatus(rse.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (ex instanceof NotFoundException) {
			return HttpStatus.NOT_FOUND;
		} else if (ex instanceof HttpServerErrorException hsee) {
			return convertToHttpStatus(hsee.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
	}

	private HttpStatus convertToHttpStatus(HttpStatusCode statusCode, HttpStatus fallback) {
		if (statusCode == null) {
			return fallback;
		}
		try {
			return HttpStatus.valueOf(statusCode.value());
		} catch (IllegalArgumentException e) {
			return fallback;
		}
	}

	private String buildErrorMessage(Throwable ex, ServerWebExchange exchange) {
		if (ex instanceof ResponseStatusException) {
			return Optional.ofNullable(((ResponseStatusException) ex).getReason())
					.orElse("Error occurred: " + ex.getMessage());
		} else if (ex instanceof NotFoundException) {
			return "The requested route was not found: " + exchange.getRequest().getPath().value();
		} else if (ex instanceof HttpServerErrorException) {
			return "Error from downstream service: " + ex.getMessage();
		}
		else {
			return "An internal server error occurred";
		}
	}

}