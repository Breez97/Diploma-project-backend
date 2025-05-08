package com.breez.exception.auth;

import com.breez.exception.CustomException;

public class InvalidPasswordException extends CustomException {

	public InvalidPasswordException(String message) {
		super(message);
	}

}
