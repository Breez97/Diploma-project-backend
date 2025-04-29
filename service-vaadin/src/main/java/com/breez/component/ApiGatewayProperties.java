package com.breez.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiGatewayProperties {

	@Value("${api.gateway.port}")
	private String port;

	public String getPort() {
		return port;
	}

}
