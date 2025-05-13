package com.breez.service.implementation;

import com.breez.dto.ProductDetailsDto;
import com.breez.dto.response.ServiceSearchResponseDto;
import com.breez.enums.Status;
import com.breez.service.ProductSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class ProductSearchServiceImplementation implements ProductSearchService {

	private static final Logger logger = LoggerFactory.getLogger(ProductSearchServiceImplementation.class);

	private final RestTemplate restTemplate;
	private final String productSearchServiceUrl;

	@Autowired
	public ProductSearchServiceImplementation(RestTemplate restTemplate, @Value("${services.service-search.url}") String productSearchServiceUrl) {
		this.restTemplate = restTemplate;
		this.productSearchServiceUrl = productSearchServiceUrl;
	}

	@Override
	public ProductDetailsDto fetchProductDetails(Long productId, String marketplaceSource) {
		String url = productSearchServiceUrl + "/" + marketplaceSource + "/product?id=" + productId;
		HttpHeaders headers = new HttpHeaders();
		String sessionId = UUID.randomUUID().toString();
		headers.set("Session-Id", sessionId);

		HttpEntity<String> entity = new HttpEntity<>(headers);
		logger.debug("Fetching product details from URL: {} with generated Session-Id: {}", url, sessionId);
		ResponseEntity<ServiceSearchResponseDto> responseEntity = restTemplate.exchange(
				url,
				HttpMethod.GET,
				entity,
				ServiceSearchResponseDto.class
		);
		ServiceSearchResponseDto responseBody = responseEntity.getBody();
		if (responseBody != null && Status.SUCCESS.getValue().equals(responseBody.getStatus()) && responseBody.getData() != null) {
			return responseBody.getData();
		}
		return null;
	}

}
