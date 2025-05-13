package com.breez.service;

import com.breez.dto.request.LoginRequest;
import com.breez.dto.request.RegisterRequest;
import com.breez.dto.request.ResendCodeRequest;
import com.breez.dto.request.VerifyRequest;
import com.breez.dto.response.AuthResponse;

public interface AuthService {

	void register(RegisterRequest request);

	void resendCode(ResendCodeRequest request);

	void verify(VerifyRequest request);

	AuthResponse login(LoginRequest request);

	void logout(String email);

	AuthResponse refreshToken(String refreshTokenValue);

}
