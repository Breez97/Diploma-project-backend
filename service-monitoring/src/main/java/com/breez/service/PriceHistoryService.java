package com.breez.service;

import com.breez.dto.request.PriceRequest;
import com.breez.dto.response.PriceHistoryResponse;

public interface PriceHistoryService {

	PriceHistoryResponse getPriceHistoryForMonitoredItem(PriceRequest request);

}
