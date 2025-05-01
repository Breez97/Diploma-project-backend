package com.breez.exception.auth;

import com.breez.exception.CustomException;

public class JwtAuthenticationException extends CustomException {

	public JwtAuthenticationException(String message) {
		super(message);
	}

}
