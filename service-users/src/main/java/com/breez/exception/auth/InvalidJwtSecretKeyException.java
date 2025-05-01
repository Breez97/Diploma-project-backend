package com.breez.exception.auth;

import com.breez.exception.CustomException;

public class InvalidJwtSecretKeyException extends CustomException {

	public InvalidJwtSecretKeyException(String message) {
		super(message);
	}

}
