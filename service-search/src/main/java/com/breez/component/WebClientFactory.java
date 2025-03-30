package com.breez.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class WebClientFactory {

	private final WebClient.Builder webClientBuilder;

	public WebClient createWebClient(String baseUrl) {
		return webClientBuilder
				.baseUrl(baseUrl)
				.build();
	}

}
