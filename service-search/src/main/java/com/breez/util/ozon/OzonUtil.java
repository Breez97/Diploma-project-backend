package com.breez.util.ozon;

import com.breez.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class OzonUtil extends CommonUtil {

	private static final String REGEX_CATEGORY = "/category/[^/]+/";

	public String extractFirstCategoryValue(String jsonResponse) {
		if (StringUtils.isBlank(jsonResponse)) {
			return null;
		}

		Pattern pattern = Pattern.compile(REGEX_CATEGORY);
		Matcher matcher = pattern.matcher(jsonResponse);

		if (!matcher.find()) {
			return null;
		}
		do {
			String category = matcher.group();
			if (!category.contains("/supermarket")) {
				return category;
			}
		} while (matcher.find());
		return null;
	}

}
