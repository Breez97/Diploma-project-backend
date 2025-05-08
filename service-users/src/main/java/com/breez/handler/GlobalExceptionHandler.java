package com.breez.handler;

import com.breez.dto.Response;
import com.breez.exception.*;
import com.breez.exception.auth.UserAlreadyExistsException;
import com.breez.exception.auth.VerificationException;
import com.breez.exception.favorite.FavoriteAlreadyExistsException;
import com.breez.exception.users.InvalidFileException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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

	@ExceptionHandler({BadCredentialsException.class,
			InvalidHeadersException.class,
			InvalidRequestBodyException.class,
			InvalidFileException.class})
	public ResponseEntity<Response<Void>> badRequestExceptionHandler(CustomException e) {
		return exceptionHandlerResponse(e, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(VerificationException.class)
	public ResponseEntity<Response<Void>> forbiddenExceptionHandler(VerificationException e) {
		return exceptionHandlerResponse(e, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<Response<Void>> noFoundExceptionHandler(UserNotFoundException e) {
		return exceptionHandlerResponse(e, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler({UserAlreadyExistsException.class,
			FavoriteAlreadyExistsException.class,
			SearchHistoryAlreadyExistException.class})
	public ResponseEntity<Response<Void>> conflictExceptionHandler(Exception e) {
		return exceptionHandlerResponse(e, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(ServerException.class)
	public ResponseEntity<Response<Void>> internalServerErrorHandlerException(ServerException e) {
		return exceptionHandlerResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Response<Void>> uncaughtExceptionHandler(Exception e) {
		return exceptionHandlerResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity<Response<Void>> exceptionHandlerResponse(Exception e, HttpStatus status) {
		return new ResponseEntity<>(Response.error(e.getMessage()), status);
	}

}
