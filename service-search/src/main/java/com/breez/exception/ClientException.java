package com.breez.exception;

import org.springframework.http.HttpStatus;

public class ClientException extends CustomException {

	public ClientException(HttpStatus errCode, String message) {
		super(errCode, message);
	}

}
