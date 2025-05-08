package com.breez.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

	@Value("${path.to.images}")
	private String pathToImages;

	@Override
	public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
		String resourcePath = "/avatars/**";
		String fileStoragePath = Paths.get(pathToImages).toUri().toString();
		if (!fileStoragePath.endsWith("/")) {
			fileStoragePath += "/";
		}
		registry.addResourceHandler(resourcePath).addResourceLocations(fileStoragePath);
	}

}
