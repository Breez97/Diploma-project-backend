package com.breez.service;

import com.breez.model.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class ResponseService {

	public static ResponseEntity<Response> successResponse(Map<String, Object> data) {
		Response response = new Response(TimestampService.getTimestamp(), Response.Status.STATUS_SUCCESS.getValue(), data);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public static ResponseEntity<Response> errorResponse(HttpStatus code, Map<String, Object> data) {
		Response response = new Response(TimestampService.getTimestamp(), Response.Status.STATUS_ERROR.getValue(), data);
		return new ResponseEntity<>(response, code);
	}

}
