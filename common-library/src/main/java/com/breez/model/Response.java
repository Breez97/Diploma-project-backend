package com.breez.model;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
