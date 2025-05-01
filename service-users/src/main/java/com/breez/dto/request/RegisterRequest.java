package com.breez.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

	@NotBlank(message = "Email can't be null")
	@Email(message = "Invalid email format")
	@Size(max = 100, message = "Email can't be longer than 100 symbols")
	private String email;

	@NotBlank(message = "Password can't be null")
	@Size(min = 8, max = 100, message = "Password must be in size between 8 and 100 symbols")
	private String password;

}
