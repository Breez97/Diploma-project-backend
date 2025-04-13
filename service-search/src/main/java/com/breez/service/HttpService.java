package com.breez.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface HttpService {

	List<Map<String, Object>> makeRequest(Map<String, String> parameters) throws IOException, InterruptedException;

	Map<String, Object> makeRequestProduct(long id) throws IOException, InterruptedException;

	Map<String, String> getSearchParameters(String title, String sort, String page);

}
