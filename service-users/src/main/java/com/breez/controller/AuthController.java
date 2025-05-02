package com.breez.controller;

import com.breez.dto.Response;
import com.breez.dto.request.LoginRequest;
import com.breez.dto.request.RegisterRequest;
import com.breez.dto.request.VerifyRequest;
import com.breez.dto.response.AuthResponse;
import com.breez.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<Response<Void>> registerUser(@Valid @RequestBody RegisterRequest request) {
		authService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(Response.success("Registration successful. Please check your email for verification code"));
	}

	@PostMapping("/verify")
	public ResponseEntity<Response<Void>> verifyAccount(@Valid @RequestBody VerifyRequest verifyRequest) {
		authService.verifyUser(verifyRequest.getEmail(), verifyRequest.getCode());
		return ResponseEntity.ok(Response.success("Account verified successfully"));
	}

	@PostMapping("/login")
	public ResponseEntity<Response<AuthResponse>> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
		AuthResponse authResponse = authService.login(loginRequest);
		return ResponseEntity.ok(Response.success(authResponse, "Login successful"));
	}

//	@PostMapping("/refresh")
//	public ResponseEntity<Response<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
//		logger.debug("POST /api/auth/refresh request received."); // Не логируем сам токен
//		try {
//			AuthResponse authResponse = authService.refreshToken(refreshTokenRequest.getRefreshToken());
//			return ResponseEntity.ok(Response.success(authResponse, "Token refreshed successfully."));
//		} catch (TokenRefreshException e) {
//			logger.warn("Token refresh failed: {}", e.getMessage());
//			return ResponseEntity
//					.status(HttpStatus.UNAUTHORIZED)
//					.body(Response.error(e.getMessage()));
//		} catch (Exception e) {
//			logger.error("Unexpected error during token refresh.", e);
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//					.body(Response.error("An internal error occurred during token refresh."));
//		}
//	}

}
