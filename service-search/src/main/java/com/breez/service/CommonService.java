package com.breez.service;

import com.breez.exception.InvalidParametersException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

import static com.breez.constants.Constants.SET_AVAILABLE_SORT;

@Service
public class CommonService {

	public void validateInputParameters(String marketplace, String title, String sort, String page) {
		validateParameter(marketplace, title, "title", StringUtils::isBlank);
		validateParameter(marketplace, sort, "sort", this::isSortInvalid);
		validateParameter(marketplace, page, "page", this::isPageInvalid);
	}

	private void validateParameter(String marketplace, String value, String parameterName, Predicate<String> validator) {
		if (validator.test(value)) {
			throw new InvalidParametersException(HttpStatus.BAD_REQUEST, marketplace + ": Invalid parameter " + parameterName);
		}
	}

	private boolean isSortInvalid(String sort) {
		return !isSortValid(sort);
	}

	private boolean isSortValid(String sort) {
		return SET_AVAILABLE_SORT.contains(sort);
	}

	private boolean isPageInvalid(String page) {
		return !isPageValid(page);
	}

	private boolean isPageValid(String page) {
		if (StringUtils.isBlank(page)) {
			return false;
		}

		for (char c : page.toCharArray()) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}

		int pageNumber = Integer.parseInt(page);
		return pageNumber > 0;
	}

}
