package com.breez.model;

import com.breez.dto.ProductDto;

import java.util.List;
import java.util.Map;

public class ProductChunkResult {

	private final List<ProductDto> products;
	private final boolean hasMore;

	public ProductChunkResult(List<ProductDto> products, boolean hasMore) {
		this.products = products;
		this.hasMore = hasMore;
	}

	public List<ProductDto> getProducts() {
		return products;
	}

	public boolean hasMore() {
		return hasMore;
	}

}
