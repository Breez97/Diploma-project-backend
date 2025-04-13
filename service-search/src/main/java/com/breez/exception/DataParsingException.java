package com.breez.exception;

import org.springframework.http.HttpStatus;

public class DataParsingException extends CustomException {

	public DataParsingException(HttpStatus errCode, String message) {
		super(errCode, message);
	}

}
