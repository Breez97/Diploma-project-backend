package com.breez.service;

import com.breez.model.ProductChunkResult;

public interface ProductsFetchingService {

	ProductChunkResult getProductChunk(MarketplaceService marketplaceService, String sessionId, String title, String sort, int chunkIndex);

}
