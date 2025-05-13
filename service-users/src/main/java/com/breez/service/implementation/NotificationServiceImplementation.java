package com.breez.service.implementation;

import com.breez.dto.request.NotificationInfoRequest;
import com.breez.dto.request.support.NotificationInfoWithEmailRequest;
import com.breez.dto.request.NotificationToggleRequest;
import com.breez.dto.request.support.NotificationToggleWithEmailRequest;
import com.breez.dto.response.NotificationResponse;
import com.breez.dto.response.ServiceNotificationsResponseDto;
import com.breez.enums.Status;
import com.breez.exception.InvalidRequestBodyException;
import com.breez.exception.ToggleServiceException;
import com.breez.mapper.ObjectMapperSingleton;
import com.breez.service.NotificationsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class NotificationServiceImplementation implements NotificationsService {

	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final RestTemplate restTemplate;

	private final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

	@Value("${services.notifications.url}")
	private String serviceNotificationsBaseUrl;

	@Override
	public NotificationResponse getNotificationInfo(String email, NotificationInfoRequest request) {
		String url = serviceNotificationsBaseUrl;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		NotificationInfoWithEmailRequest requestWithEmail = mapToInfoWithEmail(email, request);
		String requestBody;
		try {
			requestBody = objectMapper.writeValueAsString(requestWithEmail);
		} catch (JsonProcessingException e) {
			throw new InvalidRequestBodyException("Error while converting data");
		}
		HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
		ResponseEntity<ServiceNotificationsResponseDto> responseEntity = restTemplate.exchange(
				url,
				HttpMethod.GET,
				entity,
				ServiceNotificationsResponseDto.class
		);
		ServiceNotificationsResponseDto responseBody = responseEntity.getBody();
		if (responseBody != null && Status.SUCCESS.getValue().equals(responseBody.getStatus()) && responseBody.getData() != null) {
			return responseBody.getData();
		}
		throw new ToggleServiceException("Error while getting info about notification");
	}

	@Override
	public NotificationResponse toggleNotifications(String email, NotificationToggleRequest request) {
		String url = serviceNotificationsBaseUrl + "/toggle";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		NotificationToggleWithEmailRequest requestWithEmail = mapToToggleWithEmail(email, request);
		String requestBody;
		try {
			requestBody = objectMapper.writeValueAsString(requestWithEmail);
		} catch (JsonProcessingException e) {
			throw new InvalidRequestBodyException("Error while converting data");
		}
		HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
		ResponseEntity<ServiceNotificationsResponseDto> responseEntity = restTemplate.exchange(
				url,
				HttpMethod.PUT,
				entity,
				ServiceNotificationsResponseDto.class
		);
		ServiceNotificationsResponseDto responseBody = responseEntity.getBody();
		if (responseBody != null && Status.SUCCESS.getValue().equals(responseBody.getStatus()) && responseBody.getData() != null) {
			return responseBody.getData();
		}
		throw new ToggleServiceException("Error while toggling notification");
	}

	private NotificationInfoWithEmailRequest mapToInfoWithEmail(String email, NotificationInfoRequest request) {
		return NotificationInfoWithEmailRequest.builder()
				.email(email)
				.itemId(request.getItemId())
				.marketplaceSource(request.getMarketplaceSource())
				.build();
	}

	private NotificationToggleWithEmailRequest mapToToggleWithEmail(String email, NotificationToggleRequest request) {
		return NotificationToggleWithEmailRequest.builder()
				.email(email)
				.itemId(request.getItemId())
				.marketplaceSource(request.getMarketplaceSource())
				.notification(request.getNotification())
				.build();
	}

}
