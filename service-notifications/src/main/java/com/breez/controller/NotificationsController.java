package com.breez.controller;

import com.breez.dto.Response;
import com.breez.dto.request.NotificationInfoRequest;
import com.breez.dto.request.NotificationToggleRequest;
import com.breez.dto.response.NotificationsResponse;
import com.breez.service.NotificationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications Controller")
public class NotificationsController {

	private final NotificationsService notificationsService;

	@Operation(summary = "Внутреннее обращение для получения информации об уведомлении цен на товары")
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<NotificationsResponse>> getNotificationInfo(@Valid @RequestBody NotificationInfoRequest request) {
		NotificationsResponse response = notificationsService.getNotificationInfo(request);
		return ResponseEntity.ok(Response.success(response, "Notification info fetched successfully"));
	}

	@Operation(summary = "Внутреннее обращение для отключения уведомлений о снижении цен на товары")
	@PutMapping(value = "/toggle", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<NotificationsResponse>> toggleNotificationForItem(@Valid @RequestBody NotificationToggleRequest request) {
		NotificationsResponse response = notificationsService.toggleNotificationService(request);
		return ResponseEntity.ok(Response.success(response, "Toggle of the notification successfully used"));
	}

}
