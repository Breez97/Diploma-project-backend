package com.breez.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

	SUCCESS("success"),
	ERROR("error");

	private final String value;

}
