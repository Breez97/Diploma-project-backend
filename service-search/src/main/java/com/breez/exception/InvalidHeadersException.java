package com.breez.exception;

import org.springframework.http.HttpStatus;

public class InvalidHeadersException extends CustomException {

	public InvalidHeadersException(HttpStatus errCode, String message) {
		super(errCode, message);
	}

}
