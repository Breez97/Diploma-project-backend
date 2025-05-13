package com.breez.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResendCodeRequest {

	@NotBlank(message = "Email can't be null")
	@Email(message = "Invalid email format")
	@Size(max = 100, message = "Email can't be longer than 100 symbols")
	private String email;

}
