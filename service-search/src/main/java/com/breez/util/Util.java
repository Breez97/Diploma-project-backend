package com.breez.util;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public abstract class Util {

	protected String capitalizeFirstLetter(String str) {
		if (StringUtils.isBlank(str)) {
			return null;
		}
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	protected String stringOrNull(String str) {
		if (StringUtils.isBlank(str) || "0".equals(str)) {
			return null;
		}
		return str;
	}

	protected List<Object> listOrNull(List<Object> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list;
	}

}
