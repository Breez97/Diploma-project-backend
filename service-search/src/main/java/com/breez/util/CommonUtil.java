package com.breez.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class CommonUtil {

	public String capitalizeFirstLetter(String str) {
		if (StringUtils.isBlank(str)) {
			return str;
		}
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

}
