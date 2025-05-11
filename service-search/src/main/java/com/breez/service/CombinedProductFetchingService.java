package com.breez.service;

import com.breez.model.ProductChunkResult;

import java.util.List;

public interface CombinedProductFetchingService {

	ProductChunkResult getCombinedProductChunk(String sessionId, String title, String sort, int chunkIndex, List<MarketplaceService> selectedMarketplaceServices);

}
