package com.breez.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

	@NotBlank(message = "Email can't be null")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Password can't be null")
	private String password;

}
