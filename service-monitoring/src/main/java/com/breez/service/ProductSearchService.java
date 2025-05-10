package com.breez.service;

import com.breez.dto.ProductDetailsDto;

public interface ProductSearchService {

	ProductDetailsDto fetchProductDetails(Long productId, String marketplaceSource);

}
