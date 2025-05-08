package com.breez.controller;

import com.breez.dto.Response;
import com.breez.dto.request.LoginRequest;
import com.breez.dto.request.RegisterRequest;
import com.breez.dto.request.VerifyRequest;
import com.breez.dto.response.AuthResponse;
import com.breez.security.CustomUserDetails;
import com.breez.service.AuthService;
import com.breez.service.ValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/auth")
@RequiredArgsConstructor
public class AuthController {

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	private final AuthService authService;
	private final ValidationService validationService;

	@PostMapping("/register")
	public ResponseEntity<Response<Void>> registerUser(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@Valid @RequestBody RegisterRequest request) {
		validationService.validateHeaders(sessionId, logger);
		authService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(Response.success("Registration successful. Please check your email for verification code"));
	}

	@PostMapping("/verify")
	public ResponseEntity<Response<Void>> verifyAccount(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@Valid @RequestBody VerifyRequest verifyRequest) {
		validationService.validateHeaders(sessionId, logger);
		authService.verify(verifyRequest.getEmail(), verifyRequest.getCode());
		return ResponseEntity.ok(Response.success("Account verified successfully"));
	}

	@PostMapping("/login")
	public ResponseEntity<Response<AuthResponse>> loginUser(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@Valid @RequestBody LoginRequest loginRequest) {
		validationService.validateHeaders(sessionId, logger);
		AuthResponse authResponse = authService.login(loginRequest);
		return ResponseEntity.ok(Response.success(authResponse, "Login successful"));
	}

	@GetMapping("/logout")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Response<Void>> logoutUser(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		validationService.validateHeaders(sessionId, logger);
		authService.logout(userDetails.getUsername());
		return ResponseEntity.ok(Response.success("Successful logout"));
	}

}
