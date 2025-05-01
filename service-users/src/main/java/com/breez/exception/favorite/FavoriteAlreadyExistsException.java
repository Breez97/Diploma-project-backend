package com.breez.exception.favorite;

import com.breez.exception.CustomException;

public class FavoriteAlreadyExistsException extends CustomException {

	public FavoriteAlreadyExistsException(String message) {
		super(message);
	}

}
