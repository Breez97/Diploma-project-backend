package com.breez.model;

import com.breez.dto.ProductDto;
import lombok.Getter;

import java.util.List;

public class ProductChunkResult {

	@Getter
	private final List<ProductDto> products;
	private final boolean hasMore;

	public ProductChunkResult(List<ProductDto> products, boolean hasMore) {
		this.products = products;
		this.hasMore = hasMore;
	}

	public boolean hasMore() {
		return hasMore;
	}

}
