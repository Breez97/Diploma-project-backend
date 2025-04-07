package com.breez.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;

@Configuration
public class HttpClientConfiguration {

	@Bean
	public HttpClient httpClient() {
		CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
		return HttpClient.newBuilder()
				.followRedirects(HttpClient.Redirect.ALWAYS)
				.cookieHandler(cookieManager)
				.build();
	}

}
