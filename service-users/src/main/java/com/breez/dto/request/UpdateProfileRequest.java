package com.breez.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

	@Size(max = 50, message = "First name can't be longer than 50 symbols")
	private String firstName;

	@Size(max = 50, message = "Last name can't be longer than 50 symbols")
	private String lastName;

	@Size(max = 255, message = "Avatar url can't be longer than 255 symbols")
	private String avatarUrl;

}
