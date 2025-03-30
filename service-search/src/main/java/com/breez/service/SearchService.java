package com.breez.service;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface SearchService {

	Mono<List<Map<String, Object>>> wildberriesFetchData(String title);

}
