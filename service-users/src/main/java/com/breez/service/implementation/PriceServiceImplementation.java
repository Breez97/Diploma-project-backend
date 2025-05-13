package com.breez.service.implementation;

import com.breez.dto.request.PriceRequest;
import com.breez.dto.response.PriceHistoryResponse;
import com.breez.dto.response.ServiceMonitoringResponseDto;
import com.breez.enums.Status;
import com.breez.exception.NoPriceHistoryFoundException;
import com.breez.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PriceServiceImplementation implements PriceService {

	private final RestTemplate restTemplate;

	@Value("${services.monitoring.url}")
	private String serviceMonitoringBaseUrl;

	@Override
	public PriceHistoryResponse getPriceHistory(String email, PriceRequest request) {
		Long itemId = request.getItemId();
		String marketplaceSource = request.getMarketplaceSource();
		String url = serviceMonitoringBaseUrl + "?email=" + email + "&itemId=" + itemId + "&marketplaceSource=" + marketplaceSource;
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<ServiceMonitoringResponseDto> responseEntity = restTemplate.exchange(
				url,
				HttpMethod.GET,
				entity,
				ServiceMonitoringResponseDto.class
		);
		ServiceMonitoringResponseDto responseBody = responseEntity.getBody();
		if (responseBody != null && Status.SUCCESS.getValue().equals(responseBody.getStatus()) && responseBody.getData() != null) {
			return responseBody.getData();
		}
		throw new NoPriceHistoryFoundException("No price history found");
	}

}
