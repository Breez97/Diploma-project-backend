package com.breez.controller;

import com.breez.dto.Response;
import com.breez.dto.request.LoginRequest;
import com.breez.dto.request.RegisterRequest;
import com.breez.dto.request.ResendCodeRequest;
import com.breez.dto.request.VerifyRequest;
import com.breez.dto.response.AuthResponse;
import com.breez.security.CustomUserDetails;
import com.breez.service.AuthService;
import com.breez.service.ValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller")
public class AuthController {

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	private final AuthService authService;
	private final ValidationService validationService;

	@Operation(summary = "Регистрация нового пользователя с использованием почты и пароля")
	@PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<Void>> registerUser(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@Valid @RequestBody RegisterRequest request) {
		validationService.validateHeaders(sessionId, logger);
		authService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(Response.success("Registration successful. Please check your email for verification code"));
	}

	@Operation(summary = "Повторная отправка ОТП кода на почту")
	@PostMapping(value = "/resend", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<Void>> resendCode(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@Valid @RequestBody ResendCodeRequest request) {
		validationService.validateHeaders(sessionId, logger);
		authService.resendCode(request);
		return ResponseEntity.ok(Response.success("Verification code resend"));
	}

	@Operation(summary = "Верификация ОТП кода")
	@PostMapping(value = "/verify", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<Void>> verifyAccount(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@Valid @RequestBody VerifyRequest request) {
		validationService.validateHeaders(sessionId, logger);
		authService.verify(request);
		return ResponseEntity.ok(Response.success("Account verified successfully"));
	}

	@Operation(summary = "Авторизация пользователя с использованием почты и пароля")
	@PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<AuthResponse>> loginUser(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@Valid @RequestBody LoginRequest request) {
		validationService.validateHeaders(sessionId, logger);
		AuthResponse authResponse = authService.login(request);
		return ResponseEntity.ok(Response.success(authResponse, "Login successful"));
	}

	@Operation(summary = "Завершения активных сессий и выход пользователя")
	@GetMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Response<Void>> logoutUser(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		validationService.validateHeaders(sessionId, logger);
		authService.logout(userDetails.getUsername());
		return ResponseEntity.ok(Response.success("Successful logout"));
	}

}
