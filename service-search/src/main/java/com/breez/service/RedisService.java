package com.breez.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final ListOperations<String, Object> listOps;
	private final ValueOperations<String, Object> valueOps;

	private static final long DEFAULT_CACHE_TIMEOUT = 30;
	private static final TimeUnit DEFAULT_CACHE_TIMEUNIT = TimeUnit.MINUTES;

	@Autowired
	public RedisService(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
		this.listOps = redisTemplate.opsForList();
		this.valueOps = redisTemplate.opsForValue();
	}

	public void pushToList(String key, List<Object> values) {
		listOps.rightPushAll(key, values);
		redisTemplate.expire(key, DEFAULT_CACHE_TIMEOUT, DEFAULT_CACHE_TIMEUNIT);
	}

	public List<Object> getListRange(String key, long start, long end) {
		redisTemplate.expire(key, DEFAULT_CACHE_TIMEOUT, DEFAULT_CACHE_TIMEUNIT);
		return listOps.range(key, start, end);
	}

	public Long getListSize(String key) {
		return listOps.size(key);
	}

	public void saveValue(String key, Object value) {
		valueOps.set(key, value);
		redisTemplate.expire(key, DEFAULT_CACHE_TIMEOUT, DEFAULT_CACHE_TIMEUNIT);
	}

	public Object getValue(String key) {
		redisTemplate.expire(key, DEFAULT_CACHE_TIMEOUT, DEFAULT_CACHE_TIMEUNIT);
		return valueOps.get(key);
	}

	public Integer getIntValue(String key) {
		Object value = getValue(key);
		if (value instanceof Integer) {
			return (Integer) value;
		} else if (value instanceof String) {
			try {
				return Integer.parseInt((String) value);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
	}

	public void setExpire(String key) {
		redisTemplate.expire(key, DEFAULT_CACHE_TIMEOUT, DEFAULT_CACHE_TIMEUNIT);
	}

}
