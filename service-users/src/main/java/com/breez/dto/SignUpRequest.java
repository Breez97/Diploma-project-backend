package com.breez.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignUpRequest {

	@Size(min = 5, max = 50, message = "username must be between 5 and 50 characters long")
	@NotBlank(message = "username cannot be empty")
	private String username;

	@Size(min = 5, max = 255, message = "email must be between 5 and 255 characters long")
	@NotBlank(message = "email cannot be empty")
	@Email(message = "email must be in the format user@example.com")
	private String email;

	@Size(min = 6, max = 255, message = "password must be between 6 and 255 characters long")
	private String password;

}
