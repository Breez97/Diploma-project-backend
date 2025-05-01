package com.breez.dto;

import com.breez.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse<T> {

	private LocalDateTime timestamp = LocalDateTime.now();
	private String status;
	private String message;
	private T data;

	public static <T> GenericResponse<T> success(T data) {
		return success(data, "Success");
	}

	public static GenericResponse<Void> success(String message) {
		return success(null, message);
	}

	public static <T> GenericResponse<T> success(T data, String message) {
		return new GenericResponse<>(LocalDateTime.now(), Status.SUCCESS.getValue(), message, data);
	}

	public static <T> GenericResponse<T> error(String message) {
		return new GenericResponse<>(LocalDateTime.now(), Status.ERROR.getValue(), message, null);
	}

	public static <E> GenericResponse<E> error(E errorData, String message) {
		return new GenericResponse<>(LocalDateTime.now(), Status.ERROR.getValue(), message, errorData);
	}

}
