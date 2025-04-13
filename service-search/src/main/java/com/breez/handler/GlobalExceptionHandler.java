package com.breez.handler;

import com.breez.exception.*;
import com.breez.model.Response;
import com.breez.service.ResponseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({
			ClientException.class,
			DataParsingException.class,
			EmptyResponseException.class,
			InvalidParametersException.class,
			ServerException.class
	})
	public ResponseEntity<Response> customExceptionHandler(CustomException e) {
		return ResponseService.errorResponse(e.getErrorCode(), Map.of("message", e.getMessage()));
	}

}
