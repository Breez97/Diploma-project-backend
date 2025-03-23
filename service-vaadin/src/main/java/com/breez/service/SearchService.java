package com.breez.service;

import com.breez.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class SearchService {

	@Autowired
	private WebClient webClient;

	public Response search(String market) {
		return webClient.get()
				.uri("/search?market=" + market)
				.retrieve()
				.bodyToMono(Response.class)
				.block();
	}

}
