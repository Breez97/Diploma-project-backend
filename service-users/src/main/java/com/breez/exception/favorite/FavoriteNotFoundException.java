package com.breez.exception.favorite;

import com.breez.exception.CustomException;

public class FavoriteNotFoundException extends CustomException {

	public FavoriteNotFoundException(String message) {
		super(message);
	}

}
