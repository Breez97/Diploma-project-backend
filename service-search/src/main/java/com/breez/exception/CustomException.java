package com.breez.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {

	private final HttpStatus errCode;

	public CustomException(HttpStatus errCode, String message) {
		super(message);
		this.errCode = errCode;
	}

	public HttpStatus getErrorCode() {
		return errCode;
	}

}
