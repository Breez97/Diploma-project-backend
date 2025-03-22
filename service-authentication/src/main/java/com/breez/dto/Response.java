package com.breez.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class Response {

	private String timestamp;
	private String status;
	private Map<String, Object> data;

	@Getter
	public enum Status {

		STATUS_SUCCESS("success"),
		STATUS_ERROR("error");

		private final String value;

		Status(String value) {
			this.value = value;
		}

	}
}
