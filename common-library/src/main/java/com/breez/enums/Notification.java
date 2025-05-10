package com.breez.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Notification {

	DISABLE("disable"),
	ENABLE("enable"),
	REMOVE("remove");

	private final String value;

}
