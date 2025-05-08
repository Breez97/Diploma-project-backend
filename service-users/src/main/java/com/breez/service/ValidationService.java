package com.breez.service;

import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;

public interface ValidationService {

	void validateHeaders(String sessionId, Logger logger);

	void validateAvatarFile(MultipartFile file, Logger logger);

}
