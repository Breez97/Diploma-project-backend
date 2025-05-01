package com.breez.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerifyRequest {

	@NotBlank(message = "Email can't be null")
	@Email(message = "Invalid email format")
	@Size(max = 100, message = "Email can't be longer than 100 symbols")
	private String email;

	@NotNull(message = "Verification code can't be null")
	private Long code;

}
