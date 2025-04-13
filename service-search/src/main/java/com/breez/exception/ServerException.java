package com.breez.exception;

import org.springframework.http.HttpStatus;

public class ServerException extends CustomException {

	public ServerException(HttpStatus errCode, String message) {
		super(errCode, message);
	}

}
