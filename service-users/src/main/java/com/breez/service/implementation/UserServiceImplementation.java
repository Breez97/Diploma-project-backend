package com.breez.service.implementation;

import com.breez.dto.request.UpdateUserPasswordRequest;
import com.breez.dto.request.UpdateUserInfoRequest;
import com.breez.dto.response.UserResponse;
import com.breez.exception.ServerException;
import com.breez.exception.auth.InvalidPasswordException;
import com.breez.model.User;
import com.breez.repository.UserRepository;
import com.breez.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;

	@Value("${path.to.images}")
	private String pathToImages;

	@Override
	@Transactional(readOnly = true)
	public UserResponse getUserInfo(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
		return UserResponse.builder()
				.email(user.getEmail())
				.firstName(user.getFirstName())
				.lastName(user.getLastName())
				.avatarUrl(user.getAvatarUrl())
				.build();
	}

	@Override
	@Transactional
	public UserResponse updateUser(String email, UpdateUserInfoRequest request) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
		String firstName = request.getFirstName();
		String lastName = request.getLastName();
		if (StringUtils.isNotBlank(firstName)) {
			user.setFirstName(firstName);
		}
		if (StringUtils.isNotBlank(lastName)) {
			user.setLastName(lastName);
		}

		User updatedUser = userRepository.save(user);
		return UserResponse.builder()
				.email(updatedUser.getEmail())
				.firstName(updatedUser.getFirstName())
				.lastName(updatedUser.getLastName())
				.build();
	}

	@Override
	@Transactional
	public void updateUserPassword(String email, UpdateUserPasswordRequest request) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
		if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
			throw new InvalidPasswordException("Invalid password");
		}
		String newPasswordHash = passwordEncoder.encode(request.getNewPassword());
		user.setPasswordHash(newPasswordHash);
		userRepository.save(user);
	}

	@Override
	@Transactional
	public void updateUserAvatar(String email, MultipartFile file) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
		try {
			Path uploadPath = Paths.get(pathToImages);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			String originalFileName = file.getOriginalFilename();
			String fileExtension = "";
			if (originalFileName != null && originalFileName.contains(".")) {
				fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
			}

			String uniqueFileName = "user_" + user.getId() + "_" + UUID.randomUUID() + fileExtension;
			Path filePath = uploadPath.resolve(uniqueFileName);
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
			String fileAccessUrl = "/avatars/" + uniqueFileName;
			user.setAvatarUrl(fileAccessUrl);
			userRepository.save(user);
		} catch (IOException e) {
			throw new ServerException("Error with saving avatar file");
		}
	}

}
