package com.breez.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class VaadinConfiguration {

	@Value("${api.gateway.port}")
	private String apiGatewayPort;

	@Bean
	public WebClient webClient() {
		return WebClient.builder()
				.baseUrl("http://localhost:" + apiGatewayPort + "/service-search/api")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.build();
	}

}
