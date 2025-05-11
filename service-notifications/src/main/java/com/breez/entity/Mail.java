package com.breez.entity;

import com.breez.dto.event.PriceAlertEventDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Mail {

	private String receiver;
	private String subject;
	private PriceAlertEventDto event;

}
