package com.breez.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VerificationType {

	EMAIL("email"),
	PUSH("push");

	private final String value;

}
