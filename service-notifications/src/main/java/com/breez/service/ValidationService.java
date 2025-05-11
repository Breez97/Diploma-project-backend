package com.breez.service;

import com.breez.dto.event.PriceAlertEventDto;

public interface ValidationService {

	PriceAlertEventDto validatePriceAlertEvent(PriceAlertEventDto event);

}
