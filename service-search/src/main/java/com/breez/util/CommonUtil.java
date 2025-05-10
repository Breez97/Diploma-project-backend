package com.breez.util;

import com.breez.dto.ProductOptionDto;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;

public abstract class CommonUtil {

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

	protected List<ProductOptionDto> listOrNull(List<ProductOptionDto> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list;
	}

	protected BigDecimal convertPriceStringToBigDecimalOrNull(String price) {
		if (StringUtils.isNotBlank(price) && !price.trim().isEmpty() && !"null".equals(price.trim())) {
			try {
				return new BigDecimal(price.replaceAll(",", "."));
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
	}

}
