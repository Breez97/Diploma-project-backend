package com.breez.service.implementation;

import com.breez.exception.InvalidHeadersException;
import com.breez.exception.InvalidRequestBodyException;
import com.breez.exception.users.InvalidFileException;
import com.breez.service.ValidationService;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.breez.constants.Constants.MAX_AVATAR_FILE_SIZE;

@Service
public class ValidationServiceImplementation implements ValidationService {

	@Override
	public void validateHeaders(String sessionId, Logger logger) {
		if (StringUtils.isBlank(sessionId)) {
			logger.warn("invalid header Session-Id: {}", sessionId);
			throw new InvalidHeadersException("Invalid header Session-Id");
		}
	}

	@Override
	public void validateAvatarFile(MultipartFile file, Logger logger) {
		if (file.isEmpty()) {
			logger.warn("avatar file is empty");
			throw new InvalidRequestBodyException("Avatar file is empty");
		}
		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			logger.warn("invalid avatar file contentType");
			throw new InvalidFileException("Invalid avatar file contentType");
		}
		if (file.getSize() > MAX_AVATAR_FILE_SIZE) {
			logger.warn("invalid file size");
			throw new InvalidFileException("Invalid file size, need less than 5MB");
		}
	}

}
