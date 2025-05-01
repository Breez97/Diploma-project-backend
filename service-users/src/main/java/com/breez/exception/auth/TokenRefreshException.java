package com.breez.exception.auth;

import com.breez.exception.CustomException;

public class TokenRefreshException extends CustomException {

	public TokenRefreshException(String message) {
		super(message);
	}

}
