package com.breez.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommonService {

	public String getTimestamp() {
		return LocalDateTime.now().toString();
	}

}
