package com.breez.service;

import com.breez.dto.request.LoginRequest;
import com.breez.dto.request.RegisterRequest;
import com.breez.dto.response.AuthResponse;

public interface AuthService {

	void register(RegisterRequest request);

	void verify(String email, Long code);

	AuthResponse login(LoginRequest request);

	void logout(String email);

	AuthResponse refreshToken(String refreshTokenValue);

}
