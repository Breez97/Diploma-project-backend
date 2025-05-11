package com.breez.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserPasswordRequest {

	@NotBlank(message = "Password can't be null")
	@Size(min = 8, max = 100, message = "Password must be in size between 8 and 100 symbols")
	private String oldPassword;

	@NotBlank(message = "Password can't be null")
	@Size(min = 8, max = 100, message = "Password must be in size between 8 and 100 symbols")
	private String newPassword;

}
