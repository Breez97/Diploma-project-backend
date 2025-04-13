package com.breez.exception;

import org.springframework.http.HttpStatus;

public class EmptyResponseException extends CustomException {

	public EmptyResponseException(HttpStatus errCode, String message) {
		super(errCode, message);
	}

}
