package com.breez.service;

import java.time.LocalDateTime;

public class TimestampService {

	public static String getTimestamp() {
		return LocalDateTime.now().toString();
	}

}
