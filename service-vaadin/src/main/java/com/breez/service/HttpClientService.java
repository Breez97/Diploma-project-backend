package com.breez.service;

import com.breez.component.ApiGatewayProperties;
import com.breez.model.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class HttpClientService implements HttpService {

	private final HttpClient httpClient;
	private final ObjectMapper objectMapper;
	private final ApiGatewayProperties apiGatewayProperties;

	@Autowired
	public HttpClientService(HttpClient httpClient, ObjectMapper objectMapper, ApiGatewayProperties apiGatewayProperties) {
		this.httpClient = httpClient;
		this.objectMapper = objectMapper;
		this.apiGatewayProperties = apiGatewayProperties;
	}

	@Override
	public HttpResponse<String> sendGetRequest(String path) throws Exception {
		String baseUrl = "http://localhost:" + apiGatewayProperties.getPort() + "/api/v1";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(baseUrl + path))
				.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				.GET()
				.build();
		return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
	}

}
