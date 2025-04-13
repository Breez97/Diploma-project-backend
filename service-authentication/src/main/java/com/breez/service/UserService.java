package com.breez.service;

import com.breez.exception.UserAlreadyExistsException;
import com.breez.model.User;
import com.breez.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final UserRepository userRepository;

	public User save(User user) {
		return userRepository.save(user);
	}

	public User create(User user) throws UserAlreadyExistsException{
		if (userRepository.existsByUsername(user.getUsername())) {
			log.warn("user with username={} already exists", user.getUsername());
			throw new UserAlreadyExistsException("user with such username already exists");
		}

		if (userRepository.existsByEmail(user.getEmail())) {
			log.warn("user with email={} already exists", user.getEmail());
			throw new UserAlreadyExistsException("user with such email already exists");
		}

		return save(user);
	}

	public User getByUsername(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("user not found"));
	}

	public UserDetailsService userDetailsService() {
		return this::getByUsername;
	}

	public User getCurrentUser() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return getByUsername(username);
	}

	public void setAdmin() {
		User user = getCurrentUser();
		user.setRole(User.Role.ROLE_ADMIN);
		save(user);
	}

}
