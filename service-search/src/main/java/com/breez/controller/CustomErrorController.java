package com.breez.controller;

import com.breez.dto.Response;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Hidden
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class CustomErrorController extends AbstractErrorController {

	public CustomErrorController(ErrorAttributes errorAttributes) {
		super(errorAttributes);
	}

	@RequestMapping
	@ResponseBody
	public ResponseEntity<Response<Void>> handleError(HttpServletRequest request) {
		Map<String, Object> errorAttributes = getErrorAttributes(request, ErrorAttributeOptions.defaults());
		HttpStatus status = getStatus(request);
		String message = determineErrorMessage(errorAttributes, request, status);
		return ResponseEntity.status(status).body(Response.error(message));
	}

	private String determineErrorMessage(Map<String, Object> errorAttributes, HttpServletRequest request, HttpStatus status) {
		if (status == HttpStatus.NOT_FOUND) {
			String path = (String) errorAttributes.getOrDefault("path", request.getRequestURI());
			return "The requested resource was not found: " + path;
		}
		Object message = errorAttributes.get("message");
		if (message instanceof String && !((String) message).isEmpty()) {
			return (String) message;
		}
		return status.getReasonPhrase();
	}

}
