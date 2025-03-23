package com.breez.controller;

import com.breez.model.Response;
import com.breez.dto.SignInRequest;
import com.breez.dto.SignUpRequest;
import com.breez.service.AuthenticationService;
import com.breez.service.ResponseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/service-authentication/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationService authenticationService;

	@PostMapping("/sign-up")
	public ResponseEntity<Response> signUp(@RequestBody @Valid SignUpRequest request) {
		String token = authenticationService.signUp(request);
		Map<String, Object> data = Map.of("token", token);
		return ResponseService.successResponse(data);
	}

	@PostMapping("/sign-in")
	public ResponseEntity<Response> signIn(@RequestBody @Valid SignInRequest request) {
		String token = authenticationService.signIn(request);
		Map<String, Object> data = Map.of("token", token);
		return ResponseService.successResponse(data);
	}

}
