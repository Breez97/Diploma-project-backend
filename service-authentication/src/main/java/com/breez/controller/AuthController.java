package com.breez.controller;

import com.breez.dto.Response;
import com.breez.dto.SignInRequest;
import com.breez.dto.SignUpRequest;
import com.breez.service.AuthenticationService;
import com.breez.service.CommonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationService authenticationService;
	private final CommonService commonService;

	@PostMapping("/sign-up")
	public ResponseEntity<Response> signUp(@RequestBody @Valid SignUpRequest request) {
		String token = authenticationService.signUp(request);
		Map<String, Object> data = Map.of("token", token);
		Response response = new Response(commonService.getTimestamp(), Response.Status.STATUS_SUCCESS.getValue(), data);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/sign-in")
	public ResponseEntity<Response> signIn(@RequestBody @Valid SignInRequest request) {
		String token = authenticationService.signIn(request);
		Map<String, Object> data = Map.of("token", token);
		Response response = new Response(commonService.getTimestamp(), Response.Status.STATUS_SUCCESS.getValue(), data);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
