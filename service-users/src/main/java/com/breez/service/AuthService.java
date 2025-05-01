package com.breez.service;

import com.breez.dto.request.LoginRequest;
import com.breez.dto.request.RegisterRequest;
import com.breez.dto.response.AuthResponse;
import com.breez.exception.auth.UserAlreadyExistsException;
import com.breez.exception.auth.VerificationException;

public interface AuthService {

	void register(RegisterRequest request) throws UserAlreadyExistsException;

	AuthResponse login(LoginRequest request);

	AuthResponse refreshToken(String refreshTokenValue);

	void verifyUser(String email, Long code) throws VerificationException;

}
