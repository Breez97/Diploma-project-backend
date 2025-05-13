package com.breez.controller;

import com.breez.dto.Response;
import com.breez.dto.request.NotificationInfoRequest;
import com.breez.dto.request.NotificationToggleRequest;
import com.breez.dto.response.NotificationResponse;
import com.breez.security.CustomUserDetails;
import com.breez.service.NotificationsService;
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

@RestController
@RequestMapping("/api/v1/users/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications Controller")
public class NotificationsController {

	private static final Logger logger = LoggerFactory.getLogger(NotificationsController.class);

	private final NotificationsService notificationsService;
	private final ValidationService validationService;

	@Operation(summary = "Получение информации о включении/выключении уведомления (только аутентифицированный пользователь)")
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Response<NotificationResponse>> notificationInfo(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@Valid @RequestBody NotificationInfoRequest request) {
		validationService.validateHeaders(sessionId, logger);
		NotificationResponse response = notificationsService.getNotificationInfo(userDetails.getUsername(), request);
		return ResponseEntity.ok(Response.success(response, "Notification info fetched successfully"));
	}

	@Operation(summary = "Изменение уведомлений о снижении цен на товары для конкретного пользователя (только аутентифицированный пользователь)")
	@PutMapping(value = "/toggle", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Response<NotificationResponse>> notificationToggle(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@Valid @RequestBody NotificationToggleRequest request) {
		validationService.validateHeaders(sessionId, logger);
		NotificationResponse response = notificationsService.toggleNotifications(userDetails.getUsername(), request);
		return ResponseEntity.ok(Response.success(response, "Notification toggled successfully"));
	}

}
