package com.breez.service;

import java.net.http.HttpResponse;

public interface HttpService {

	public HttpResponse<String> sendGetRequest(String path) throws Exception;

}
