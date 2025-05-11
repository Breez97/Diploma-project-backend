package com.breez.util.marketplace.ozon;

import com.breez.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.breez.constants.Constants.OZON_EXTERNAL_LINK;

@Component
public class OzonUtil extends CommonUtil {

	private static final String REGEX_CATEGORY = "/category/[^/]+/";
	private static final String REGEX_NUMBERS = "\\D";

	protected String getExternalLinkOzon(long id) {
		return OZON_EXTERNAL_LINK + "/product/" + id;
	}

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

	protected String extractDigitsFromString(String str) {
		if (str == null || str.isEmpty()) {
			return null;
		}
		return str.replaceAll(REGEX_NUMBERS, "");
	}

}
