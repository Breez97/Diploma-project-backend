package com.breez.service.marketplace;

import com.breez.dto.ProductDto;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface MarketplaceService {

	Map<String, String> getSearchParameters(String title, String sort, String page);

	List<ProductDto> fetchProducts(Map<String, String> parameters) throws IOException, InterruptedException;

	String getMarketplaceIdentifier();

}
