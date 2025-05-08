package com.breez.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserInfoRequest {

	@Size(max = 50, message = "First name can't be longer than 50 symbols")
	private String firstName;

	@Size(max = 50, message = "Last name can't be longer than 50 symbols")
	private String lastName;

}
