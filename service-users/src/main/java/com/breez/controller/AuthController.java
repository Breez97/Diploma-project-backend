package com.breez.controller;

import com.breez.dto.GenericResponse;
import com.breez.dto.request.LoginRequest;
import com.breez.dto.request.RegisterRequest;
import com.breez.dto.request.VerifyRequest;
import com.breez.dto.response.AuthResponse;
import com.breez.exception.auth.UserAlreadyExistsException;
import com.breez.exception.auth.VerificationException;
import com.breez.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	private final AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<GenericResponse<Void>> registerUser(@Valid @RequestBody RegisterRequest request) {
		try {
			authService.register(request);
			return ResponseEntity
					.status(HttpStatus.CREATED)
					.body(GenericResponse.success("Registration successful. Please check your email for verification code"));
		} catch (UserAlreadyExistsException e) {
			logger.error("Registration failed: {}", e.getMessage());
			return ResponseEntity
					.status(HttpStatus.CONFLICT)
					.body(GenericResponse.error(e.getMessage()));
		} catch (Exception e) {
			logger.error("Unexpected error during registration for email: {}", request.getEmail(), e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(GenericResponse.error("An internal error occurred during registration."));
		}
	}

	@PostMapping("/login")
	public ResponseEntity<GenericResponse<AuthResponse>> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
		try {
			AuthResponse authResponse = authService.login(loginRequest);
			return ResponseEntity.ok(GenericResponse.success(authResponse, "Login successful."));
		} catch (VerificationException e) {
			logger.warn("Login failed for email {}: {}", loginRequest.getEmail(), e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(GenericResponse.error(e.getMessage()));
		} catch (Exception e) {
			logger.error("Unexpected error during login for email: {}", loginRequest.getEmail(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(GenericResponse.error("An internal error occurred during login."));
		}
	}

	@PostMapping("/verify")
	public ResponseEntity<GenericResponse<Void>> verifyAccount(@Valid @RequestBody VerifyRequest verifyRequest) {
		try {
			authService.verifyUser(verifyRequest.getEmail(), verifyRequest.getCode());
			return ResponseEntity.ok(GenericResponse.success("Account verified successfully. You can now log in"));
		} catch (VerificationException e) {
			logger.warn("Verification failed: {}", e.getMessage());
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(GenericResponse.error(e.getMessage()));
		} catch (Exception e) {
			logger.error("Unexpected error during verification for code: {}", verifyRequest.getCode(), e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(GenericResponse.error("An internal error occurred during verification"));
		}
	}

//	@PostMapping("/refresh")
//	public ResponseEntity<GenericResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
//		logger.debug("POST /api/auth/refresh request received."); // Не логируем сам токен
//		try {
//			AuthResponse authResponse = authService.refreshToken(refreshTokenRequest.getRefreshToken());
//			return ResponseEntity.ok(GenericResponse.success(authResponse, "Token refreshed successfully."));
//		} catch (TokenRefreshException e) {
//			logger.warn("Token refresh failed: {}", e.getMessage());
//			return ResponseEntity
//					.status(HttpStatus.UNAUTHORIZED)
//					.body(GenericResponse.error(e.getMessage()));
//		} catch (Exception e) {
//			logger.error("Unexpected error during token refresh.", e);
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//					.body(GenericResponse.error("An internal error occurred during token refresh."));
//		}
//	}

}
