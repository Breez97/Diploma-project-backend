package com.breez.service.implementation;

import com.breez.exception.InvalidHeadersException;
import com.breez.service.ValidationService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class ValidationServiceImplementation implements ValidationService {

	@Override
	public void validateHeaders(String sessionId, Logger logger) {
		if (StringUtils.isBlank(sessionId)) {
			logger.warn("invalid header Session-Id: {}", sessionId);
			throw new InvalidHeadersException("Invalid header Session-Id");
		}
	}

}
