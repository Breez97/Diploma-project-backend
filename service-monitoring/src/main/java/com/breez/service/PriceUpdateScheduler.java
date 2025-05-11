package com.breez.service;

import com.breez.model.MonitoredItem;

public interface PriceUpdateScheduler {

	void updatePricesAndNotify();

	void updatePriceForSpecificItem(MonitoredItem item);

}
