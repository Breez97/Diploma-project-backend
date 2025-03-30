package com.breez.service;

import com.breez.model.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class SearchService {

	private final WebClient webClient;

	public Response search(String market) {
		return webClient.get()
				.uri("/search?market=" + market)
				.retrieve()
				.bodyToMono(Response.class)
				.block();
	}

}
