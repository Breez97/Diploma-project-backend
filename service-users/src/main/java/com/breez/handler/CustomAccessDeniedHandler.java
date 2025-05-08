package com.breez.handler;

import com.breez.dto.Response;
import com.breez.exception.ServerException;
import com.breez.mapper.ObjectMapperSingleton;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
		Response<Void> apiResponse = Response.error("Access denied");
		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");

		try {
			String jsonResponse = objectMapper.writeValueAsString(apiResponse);
			response.getWriter().write(jsonResponse);
			response.getWriter().flush();
		} catch (IOException e) {
			throw new ServerException(e.getMessage());
		}
	}

}
