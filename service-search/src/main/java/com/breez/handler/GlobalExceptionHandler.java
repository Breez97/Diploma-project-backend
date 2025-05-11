package com.breez.handler;

import com.breez.dto.Response;
import com.breez.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Response<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException e) {
		Map<String, String> errors = new HashMap<>();
		e.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		Response<Map<String, String>> response = Response.error(errors, "Validation failed");
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({EmptyResponseException.class, NoProductsFoundException.class})
	public ResponseEntity<Response<Void>> notFoundExceptionHandler(CustomException e) {
		return exceptionHandlerResponse(e, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler({ClientException.class, InvalidHeadersException.class})
	public ResponseEntity<Response<Void>> badRequestExceptionHandler(CustomException e) {
		return exceptionHandlerResponse(e, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({DataParsingException.class, ServerException.class})
	public ResponseEntity<Response<Void>> internalServerErrorExceptionHandler(CustomException e) {
		return exceptionHandlerResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler
	public ResponseEntity<Response<Void>> uncaughtExceptionHandler(Exception e) {
		return exceptionHandlerResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity<Response<Void>> exceptionHandlerResponse(Exception e, HttpStatus status) {
		return new ResponseEntity<>(Response.error(e.getMessage()), status);
	}

}
