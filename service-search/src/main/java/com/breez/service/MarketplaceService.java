package com.breez.service;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface MarketplaceService {

	Mono<List<Map<String, Object>>> fetchData(String title);
	Mono<List<Map<String, Object>>> fetchData(String title, Map<String, Object> params);

	Map<String, Object> createDefaultParams();

}
