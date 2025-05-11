package com.breez.service.implementation;

import com.breez.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.breez.constants.Constants.DEFAULT_CACHE_TIMEOUT;
import static com.breez.constants.Constants.DEFAULT_CACHE_TIMEUNIT;

@Service
public class RedisServiceImplementation implements RedisService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final ListOperations<String, Object> listOps;
	private final ValueOperations<String, Object> valueOps;

	@Autowired
	public RedisServiceImplementation(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
		this.listOps = redisTemplate.opsForList();
		this.valueOps = redisTemplate.opsForValue();
	}

	@Override
	public void pushToList(String key, List<Object> values) {
		listOps.rightPushAll(key, values);
		redisTemplate.expire(key, DEFAULT_CACHE_TIMEOUT, DEFAULT_CACHE_TIMEUNIT);
	}

	@Override
	public List<Object> getListRange(String key, long start, long end) {
		redisTemplate.expire(key, DEFAULT_CACHE_TIMEOUT, DEFAULT_CACHE_TIMEUNIT);
		return listOps.range(key, start, end);
	}

	@Override
	public Long getListSize(String key) {
		return listOps.size(key);
	}

	@Override
	public void saveValue(String key, Object value) {
		valueOps.set(key, value);
		redisTemplate.expire(key, DEFAULT_CACHE_TIMEOUT, DEFAULT_CACHE_TIMEUNIT);
	}

	@Override
	public Object getValue(String key) {
		redisTemplate.expire(key, DEFAULT_CACHE_TIMEOUT, DEFAULT_CACHE_TIMEUNIT);
		return valueOps.get(key);
	}

	@Override
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

	@Override
	public void setExpire(String key) {
		redisTemplate.expire(key, DEFAULT_CACHE_TIMEOUT, DEFAULT_CACHE_TIMEUNIT);
	}

}
