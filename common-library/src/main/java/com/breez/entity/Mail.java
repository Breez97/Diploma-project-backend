package com.breez.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Mail {

	private String receiver;
	private String subject;
	private String code;

}
