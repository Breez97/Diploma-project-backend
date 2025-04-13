package com.breez.exception;

import org.springframework.http.HttpStatus;

public class InvalidParametersException extends CustomException {

	public InvalidParametersException(HttpStatus errCode, String message) {
		super(errCode, message);
	}

}
