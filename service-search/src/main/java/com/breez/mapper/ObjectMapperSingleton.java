package com.breez.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperSingleton {

	private static ObjectMapper INSTANCE;

	private ObjectMapperSingleton() {}

	public static ObjectMapper getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ObjectMapper();
		}
		return INSTANCE;
	}

}
