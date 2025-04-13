package com.breez.handler;

import com.breez.model.Response;
import com.breez.exception.UserAlreadyExistsException;
import com.breez.service.ResponseService;
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

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<Response> authenticationExceptionHandler(AuthenticationException e) {
		return ResponseService.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, Map.of("message", e.getMessage()));
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<Response> userAlreadyExistsExceptionHandler(UserAlreadyExistsException e) {
		return ResponseService.errorResponse(HttpStatus.CONFLICT, Map.of("message", e.getMessage()));
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<Response> usernameNotFoundExceptionHandler(UsernameNotFoundException e) {
		return ResponseService.errorResponse(HttpStatus.NOT_FOUND, Map.of("message", e.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Response> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
		List<String> errors = e.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(FieldError::getDefaultMessage)
				.toList();

		Map<String, Object> data = Map.of("errors", errors);
		return ResponseService.errorResponse(HttpStatus.FORBIDDEN, data);
	}

}
