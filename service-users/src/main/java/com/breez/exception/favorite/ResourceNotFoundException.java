package com.breez.exception.favorite;

import com.breez.exception.CustomException;

public class ResourceNotFoundException extends CustomException {

	public ResourceNotFoundException(String message) {
		super(message);
	}

}
