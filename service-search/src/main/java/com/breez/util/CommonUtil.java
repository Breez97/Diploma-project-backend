package com.breez.util;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CommonUtil extends Util {

	public Map<String, Object> getEmptyData(long id) {
		Map<String, Object> emptyData = new LinkedHashMap<>();
		emptyData.put("id", id);
		emptyData.put("externalLink", null);
		emptyData.put("title", null);
		emptyData.put("imageUrl", null);
		emptyData.put("brand", null);
		emptyData.put("price", null);
		emptyData.put("rating", null);
		emptyData.put("feedbacks", null);
		emptyData.put("marketplace", null);
		return emptyData;
	}

}
