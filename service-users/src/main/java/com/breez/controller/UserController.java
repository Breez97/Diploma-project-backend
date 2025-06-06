package com.breez.controller;

import com.breez.dto.Response;
import com.breez.dto.request.UpdateUserPasswordRequest;
import com.breez.dto.request.UpdateUserInfoRequest;
import com.breez.dto.response.UserResponse;
import com.breez.security.CustomUserDetails;
import com.breez.service.UserService;
import com.breez.service.ValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users/info")
@RequiredArgsConstructor
@Tag(name = "User Controller")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	private final UserService userService;
	private final ValidationService validationService;

	@Operation(summary = "Получение личной информации пользователя (только аутентифицированный пользователь)")
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Response<UserResponse>> getUserInfo(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		validationService.validateHeaders(sessionId, logger);
		UserResponse response = userService.getUserInfo(userDetails.getUsername());
		return ResponseEntity.ok(Response.success(response, "User info received successfully"));
	}

	@Operation(summary = "Обновление личной информации о конкретном пользователе (только аутентифицированный пользователь)")
	@PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Response<UserResponse>> updateUser(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@Valid @RequestBody UpdateUserInfoRequest request) {
		validationService.validateHeaders(sessionId, logger);
		UserResponse response = userService.updateUser(userDetails.getUsername(), request);
		return ResponseEntity.ok(Response.success(response, "User info updates successfully"));
	}

	@Operation(summary = "Обновление пароля для кокнретного пользователя (только аутентифицированный пользователь)")
	@PutMapping(value = "/update/password", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Response<Void>> updateUserPassword(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@Valid @RequestBody UpdateUserPasswordRequest request) {
		validationService.validateHeaders(sessionId, logger);
		userService.updateUserPassword(userDetails.getUsername(), request);
		return ResponseEntity.ok(Response.success("User password updated successfully"));
	}

	@Operation(summary = "Обновление аватара для конкретного пользователя (только аутентифицированный пользователь)")
	@PutMapping(value = "/update/avatar", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Response<Void>> updateAvatar(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestParam("avatarFile") MultipartFile avatarFile) {
		validationService.validateHeaders(sessionId, logger);
		validationService.validateAvatarFile(avatarFile, logger);
		userService.updateUserAvatar(userDetails.getUsername(), avatarFile);
		return ResponseEntity.ok(Response.success("User avatar updated successfully"));
	}

}
