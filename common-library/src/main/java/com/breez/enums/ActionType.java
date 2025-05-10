package com.breez.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActionType {

	ADD("add"),
	REMOVE("remove");

	private final String value;

}
