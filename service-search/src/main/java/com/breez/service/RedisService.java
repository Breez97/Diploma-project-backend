package com.breez.service;

import java.util.List;

public interface RedisService {

	void pushToList(String key, List<Object> values);

	List<Object> getListRange(String key, long start, long end);

	Long getListSize(String key);

	void saveValue(String key, Object value);

	Object getValue(String key);

	Integer getIntValue(String key);

	void setExpire(String key);

}
