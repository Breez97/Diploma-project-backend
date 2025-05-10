package com.breez.handler;

import com.breez.dto.Response;
import com.breez.exception.CustomException;
import com.breez.exception.NotificationItemNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NotificationItemNotFoundException.class)
	public ResponseEntity<Response<Void>> notFoundExceptionHandler(CustomException e) {
		return exceptionHandlerResponse(e, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Response<Void>> uncaughtExceptionHandler(Exception e) {
		return exceptionHandlerResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity<Response<Void>> exceptionHandlerResponse(Exception e, HttpStatus status) {
		return new ResponseEntity<>(Response.error(e.getMessage()), status);
	}

}
