package com.breez.controller;

import com.breez.dto.Response;
import com.breez.dto.request.ToggleNotificationRequest;
import com.breez.dto.response.ToggleNotificationsResponse;
import com.breez.service.ToggleNotificationsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/notifications")
@RequiredArgsConstructor
public class NotificationsController {

	private final ToggleNotificationsService toggleNotificationsService;

	@PostMapping("/toggle")
	public ResponseEntity<Response<ToggleNotificationsResponse>> toggleNotificationForItem(
			@Valid @RequestBody ToggleNotificationRequest request) {
		ToggleNotificationsResponse response = toggleNotificationsService.toggleNotificationService(request);
		return ResponseEntity.ok(Response.success(response, "Toggle of the notification successfully changed"));
	}

}
