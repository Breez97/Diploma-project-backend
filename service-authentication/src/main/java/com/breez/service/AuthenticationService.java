package com.breez.service;

import com.breez.dto.SignInRequest;
import com.breez.dto.SignUpRequest;
import com.breez.exception.UserAlreadyExistsException;
import com.breez.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

	private final UserService userService;
	private final JwtService jwtService;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;

	public String signUp(SignUpRequest request) throws UserAlreadyExistsException {
		User user = User.builder()
				.username(request.getUsername())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(User.Role.ROLE_USER)
				.build();

		if (userService.create(user) != null) {
			log.info("user created successfully, username={}, email={}", user.getUsername(), user.getEmail());
		} else {
			log.error("error creating user, with username={}, email={}", user.getUsername(), user.getEmail());
		}

		return jwtService.generateToken(user);
	}

	public String signIn(SignInRequest request) throws AuthenticationException {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				request.getUsername(),
				request.getPassword()
		));

		UserDetails user = userService
				.userDetailsService()
				.loadUserByUsername(request.getUsername());

		return jwtService.generateToken(user);
	}

}
