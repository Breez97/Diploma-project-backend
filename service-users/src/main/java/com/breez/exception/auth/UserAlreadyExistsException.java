package com.breez.exception.auth;

import com.breez.exception.CustomException;

public class UserAlreadyExistsException extends CustomException {

	public UserAlreadyExistsException(String message) {
		super(message);
	}

}
