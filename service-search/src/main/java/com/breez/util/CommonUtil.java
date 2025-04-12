package com.breez.util;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.breez.constants.Constants.*;

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

	protected List<Object> listOrNull(List<Object> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list;
	}

	protected Map<String, Object> getEmptyDataAllProducts(long id) {
		Map<String, Object> emptyData = new LinkedHashMap<>();
		emptyData.put("id", id);
		emptyData.put("externalLink", null);
		emptyData.put("title", null);
		emptyData.put("imageUrl", null);
		emptyData.put("brand", null);
		emptyData.put("price", null);
		emptyData.put("rating", null);
		emptyData.put("feedbacks", null);
		return emptyData;
	}

	protected Map<String, Object> getEmptyDataSingleProduct(long id) {
		Map<String, Object> emptyData = new LinkedHashMap<>();
		emptyData.put("id", id);
		emptyData.put("externalLink", null);
		emptyData.put("title", null);
		emptyData.put("imageUrl", null);
		emptyData.put("description", null);
		emptyData.put("options", null);
		return emptyData;
	}

	protected String getExternalLinkOzon(long id) {
		return OZON_EXTERNAL_LINK + "/product/" + id;
	}

}
