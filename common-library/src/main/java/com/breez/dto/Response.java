package com.breez.dto;

import com.breez.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {

	private LocalDateTime timestamp = LocalDateTime.now();
	private String status;
	private String message;
	private T data;

	public static <T> Response<T> success(T data) {
		return success(data, "Success");
	}

	public static Response<Void> success(String message) {
		return success(null, message);
	}

	public static <T> Response<T> success(T data, String message) {
		return new Response<>(LocalDateTime.now(), Status.SUCCESS.getValue(), message, data);
	}

	public static <T> Response<T> error(String message) {
		return new Response<>(LocalDateTime.now(), Status.ERROR.getValue(), message, null);
	}

	public static <E> Response<E> error(E errorData, String message) {
		return new Response<>(LocalDateTime.now(), Status.ERROR.getValue(), message, errorData);
	}

}
