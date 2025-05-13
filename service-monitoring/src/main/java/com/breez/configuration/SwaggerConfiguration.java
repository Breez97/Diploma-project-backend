package com.breez.configuration;

import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
@ComponentScan(basePackages = {"com.breez.controller"})
public class SwaggerConfiguration {

	@Bean
	public OpenApiCustomizer customOpenApiServerUrlCustomizer() {
		return openApi -> {
			Server gatewayServer = new Server();
			gatewayServer.setUrl("/");
			openApi.setServers(Collections.singletonList(gatewayServer));
		};
	}

}
