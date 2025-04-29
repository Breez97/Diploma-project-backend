package com.breez.model;

import java.util.List;
import java.util.Map;

public class ProductChunkResult {

	private final List<Map<String, Object>> products;
	private final boolean hasMore;

	public ProductChunkResult(List<Map<String, Object>> products, boolean hasMore) {
		this.products = products;
		this.hasMore = hasMore;
	}

	public List<Map<String, Object>> getProducts() {
		return products;
	}

	public boolean hasMore() {
		return hasMore;
	}

}
