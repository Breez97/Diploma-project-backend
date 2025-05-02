package com.breez.handler;

import com.breez.dto.Response;
import com.breez.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NoProductsFoundException.class)
	public ResponseEntity<Response<Void>> noProductsFoundExceptionHandler(NoProductsFoundException e) {
		return exceptionHandlerResponse(e, HttpStatus.NOT_FOUND);
	}

	private ResponseEntity<Response<Void>> exceptionHandlerResponse(Exception e, HttpStatus status) {
		return new ResponseEntity<>(Response.error(e.getMessage()), status);
	}

}
