package com.breez.service;

import com.breez.exception.InvalidHeadersException;
import com.breez.exception.InvalidParametersException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

import static com.breez.constants.Constants.SET_ALLOWED_MARKETPLACES;
import static com.breez.constants.Constants.SET_ALLOWED_SORT;

@Service
public class ValidationService {

	private static final String MARKETPLACE_REGEX = "^[a-zA-Z]+(,[a-zA-Z]+)*$";

	public void validateMarketplaces(String marketplaces, Logger logger) {
		if (!validateParameter(marketplaces, this::isMarketplacesInValid)) {
			logger.error("invalid marketplaces: {}", marketplaces);
			throw new InvalidParametersException(HttpStatus.BAD_REQUEST, "Invalid marketplaces");
		}
	}

	public void validateHeaders(String sessionId, Logger logger) {
		if (StringUtils.isBlank(sessionId)) {
			logger.error("invalid sessionId: {}", sessionId);
			throw new InvalidHeadersException(HttpStatus.BAD_REQUEST, "Invalid sessionId");
		}
	}

	public void validateInputParameters(String title, String sort, String chunk, Logger logger) {
		Set<String> invalidParameters = new LinkedHashSet<>();
		boolean isInvalid = false;
		if (StringUtils.isBlank(title)) {
			invalidParameters.add("title");
			isInvalid = true;
		}
		if (!validateParameter(sort, this::isSortInvalid)) {
			invalidParameters.add("sort");
			isInvalid = true;
		}
		if (!validateParameter(chunk, this::isChunkValid)) {
			invalidParameters.add("page");
			isInvalid = true;
		}
		if (isInvalid) {
			logger.error("Invalid parameters: {}", invalidParameters);
			throw new InvalidParametersException(HttpStatus.BAD_REQUEST, "Invalid parameters: " + invalidParameters);
		}
	}

	private boolean validateParameter(String value, Predicate<String> validator) {
		return !validator.test(value);
	}

	private boolean isMarketplacesInValid(String marketplaces) {
		if (!marketplaces.matches(MARKETPLACE_REGEX)) {
			return true;
		}
		String formattedMarketplaces = marketplaces.toLowerCase();
		String[] marketplacesArray = formattedMarketplaces.split(",");
		for (String marketplace : marketplacesArray) {
			if (!SET_ALLOWED_MARKETPLACES.contains(marketplace)) {
				return true;
			}
		}
		return false;
	}

	private boolean isSortInvalid(String sort) {
		return !SET_ALLOWED_SORT.contains(sort);
	}

	private boolean isChunkValid(String page) {
		return !isPageValid(page);
	}

	private boolean isPageValid(String chunk) {
		for (char c : chunk.toCharArray()) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}

		int chunkNumber = Integer.parseInt(chunk);
		return chunkNumber > 0;
	}

}
