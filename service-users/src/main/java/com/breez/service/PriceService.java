package com.breez.service;

import com.breez.dto.request.PriceRequest;
import com.breez.dto.response.PriceHistoryResponse;

public interface PriceService {

	PriceHistoryResponse getPriceHistory(String email, PriceRequest request);

}
