package com.breez.controller;

import com.breez.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {

	private final UserService userService;

	@GetMapping
	public String test() {
		return "Hello";
	}

	@GetMapping("/admin")
	public String testAdmin() {
		return "Hello, Admin";
	}

	@GetMapping("/get-admin")
	public void getAdmin() {
		userService.setAdmin();
	}

}
