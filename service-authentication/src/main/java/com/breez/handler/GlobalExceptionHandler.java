package com.breez.handler;

import com.breez.dto.Response;
import com.breez.exception.UserAlreadyExistsException;
import com.breez.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	private final CommonService commonService;

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<Response> authenticationExceptionHandler(AuthenticationException e) {
		return errorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<Response> userAlreadyExistsExceptionHandler(UserAlreadyExistsException e) {
		return errorResponse(e.getMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<Response> usernameNotFoundExceptionHandler(UsernameNotFoundException e) {
		return errorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Response> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
		List<String> errors = e.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(FieldError::getDefaultMessage)
				.toList();

		Map<String, Object> data = Map.of("errors", errors);
		Response response = new Response(commonService.getTimestamp(), Response.Status.STATUS_ERROR.getValue(), data);

		return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
	}

	private ResponseEntity<Response> errorResponse(String errorMessage, HttpStatus code) {
		Map<String, Object> data = Map.of("message", errorMessage);
		Response response = new Response(commonService.getTimestamp(), Response.Status.STATUS_ERROR.getValue(), data);
		return new ResponseEntity<>(response, code);
	}

}
