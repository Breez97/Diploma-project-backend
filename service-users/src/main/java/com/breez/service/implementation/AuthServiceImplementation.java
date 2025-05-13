package com.breez.service.implementation;

import com.breez.dto.request.LoginRequest;
import com.breez.dto.request.RegisterRequest;
import com.breez.dto.request.ResendCodeRequest;
import com.breez.dto.request.VerifyRequest;
import com.breez.dto.response.AuthResponse;
import com.breez.entity.Mail;
import com.breez.enums.Role;
import com.breez.enums.VerificationType;
import com.breez.exception.auth.InvalidPasswordException;
import com.breez.exception.auth.TokenRefreshException;
import com.breez.exception.auth.UserAlreadyExistsException;
import com.breez.exception.auth.VerificationException;
import com.breez.exception.UserNotFoundException;
import com.breez.model.RefreshToken;
import com.breez.model.User;
import com.breez.model.UserVerification;
import com.breez.repository.RefreshTokenRepository;
import com.breez.repository.UserRepository;
import com.breez.repository.UserVerificationRepository;
import com.breez.security.CustomUserDetails;
import com.breez.service.AuthService;
import com.breez.service.MailService;
import com.breez.util.JwtTokenProvider;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.breez.constants.Constants.SKIP_VERIFICATION_CODE_VALUE;
import static com.breez.constants.Constants.VERIFICATION_CODE_EXPIRATION_MINUTES;

@Service
@RequiredArgsConstructor
public class AuthServiceImplementation implements AuthService {

	private static final Logger logger = LoggerFactory.getLogger(AuthServiceImplementation.class);

	private final AuthenticationManager authenticationManager;
	private final MailService mailService;
	private final JwtTokenProvider jwtTokenProvider;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;
	private final UserVerificationRepository userVerificationRepository;

	private final SecureRandom random = new SecureRandom();

	@Value("${skip.code.verification:false}")
	private boolean skipCodeVerification;

	@Override
	@Transactional
	public void register(RegisterRequest request) {
		String email = request.getEmail();
		if (userRepository.existsByEmail(email)) {
			logger.error("Registration failed: email {} already exists", email);
			throw new UserAlreadyExistsException("Email " + email + " is already registered");
		}

		User newUser = User.builder()
				.email(email)
				.passwordHash(passwordEncoder.encode(request.getPassword()))
				.role(Role.USER)
				.enabled(false)
				.firstName(null)
				.lastName(null)
				.build();

		UserVerification userVerification = getUserVerification(newUser, null);
		userRepository.save(newUser);
		userVerificationRepository.save(userVerification);
	}

	@Override
	@Transactional
	public void resendCode(ResendCodeRequest request) {
		String email = request.getEmail();
		User referenceUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
		UserVerification userVerification = userVerificationRepository.findByUserId(referenceUser.getId())
				.orElseThrow(() -> new VerificationException("User verification not found with email: " + email));

		UserVerification newUserVerification = getUserVerification(referenceUser, userVerification);
		userVerificationRepository.save(newUserVerification);
	}

	@Override
	@Transactional
	public void verify(VerifyRequest request) {
		String email = request.getEmail();
		Long code = request.getCode();
		if (skipCodeVerification) {
			code = SKIP_VERIFICATION_CODE_VALUE;
		}
		User referenceUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
		UserVerification verification = userVerificationRepository.findByUserIdAndCode(referenceUser.getId(), code)
				.orElseThrow(() -> new VerificationException("Invalid verification code"));
		if (verification.getExpiryTime().isBefore(LocalDateTime.now())) {
			logger.warn("Verification failed: code {} has expired", code);
			userVerificationRepository.delete(verification);
			throw new VerificationException("Verification code has expired");
		}

		User user = verification.getUser();
		if (user == null || !user.getEmail().equalsIgnoreCase(email)) {
			logger.error("Verification failed: Code {} doesn't belong to user {}", code, email);
			userVerificationRepository.delete(verification);
			throw new VerificationException("Internal error during verification");
		}

		user.setEnabled(true);
		userRepository.save(user);
		userVerificationRepository.delete(verification);
	}

	@Override
	@Transactional
	public AuthResponse login(LoginRequest request) {
		String email = request.getEmail();
		String password = request.getPassword();
		User userDatabase = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
		if (!passwordEncoder.matches(password, userDatabase.getPasswordHash())) {
			throw new InvalidPasswordException("Invalid password");
		}

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(email, password)
		);

		SecurityContextHolder.getContext().setAuthentication(authentication);
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		User user = userDetails.getUserEntity();
		if (!user.isEnabled()) {
			logger.warn("Login failed: User {} is not verified", user.getEmail());
			throw new VerificationException("Account is not verified");
		}

		String accessToken = jwtTokenProvider.generateAccessToken(authentication);
		String refreshTokenValue = jwtTokenProvider.generateRefreshToken(authentication);
		RefreshToken refreshTokenEntity = RefreshToken.builder()
				.user(user)
				.token(refreshTokenValue)
				.expiryDate(jwtTokenProvider.getRefreshExpirationLocalDateTime())
				.revoked(false)
				.build();
		refreshTokenRepository.save(refreshTokenEntity);
		return mapToDto(user.getId(), accessToken, refreshTokenValue);
	}

	@Override
	@Transactional
	public void logout(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
		revokeAllUsersTokens(user);
	}

	@Override
	@Transactional
	public AuthResponse refreshToken(String refreshTokenValue) {
		RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(refreshTokenValue)
				.orElseThrow(() -> new TokenRefreshException("Refresh token not found in database: " + refreshTokenValue));

		if (refreshTokenEntity.isRevoked()) {
			logger.warn("Attempted to use a revoked refresh token for user: {}", refreshTokenEntity.getUser().getEmail());
			refreshTokenRepository.delete(refreshTokenEntity);
			throw new TokenRefreshException("Refresh token was revoked: " + refreshTokenValue);
		}

		if (refreshTokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
			logger.warn("Refresh token has expired: {}", refreshTokenValue);
			refreshTokenRepository.delete(refreshTokenEntity);
			throw new TokenRefreshException("Refresh token has expired: " + refreshTokenValue);
		}

		User user = refreshTokenEntity.getUser();
		String newAccessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
		return mapToDto(user.getId(), newAccessToken, refreshTokenValue);
	}

	private UserVerification getUserVerification(User user, UserVerification userVerification) {
		long verificationCode = generateVerificationCode();
		if (skipCodeVerification) {
			verificationCode = SKIP_VERIFICATION_CODE_VALUE;
		}
		LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(VERIFICATION_CODE_EXPIRATION_MINUTES);

		UserVerification newUserVerification = UserVerification.builder()
				.user(user)
				.code(verificationCode)
				.verificationType(VerificationType.EMAIL)
				.expiryTime(expiryTime)
				.build();
		if (userVerification != null) {
			newUserVerification.setId(userVerification.getId());
		}

		logger.info("Generated new verification code {{}} for user {}", verificationCode, user.getEmail());
		if (!skipCodeVerification) {
			try {
				Mail mail = Mail.builder().receiver(user.getEmail()).subject("EasyFind: Verification code").code(String.valueOf(verificationCode)).build();
				mailService.sendEmailWithThymeleaf(mail);
			} catch (MailException | MessagingException e) {
				logger.error(Arrays.toString(e.getStackTrace()));
			}
		}
		return newUserVerification;
	}

	private long generateVerificationCode() {
		return 100000 + random.nextInt(900000);
	}

	private void revokeAllUsersTokens(User user) {
		List<RefreshToken> validUserTokens = refreshTokenRepository.findByUserAndRevokedFalse(user);
		if (validUserTokens.isEmpty()) {
			return;
		}
		validUserTokens.forEach(token -> token.setRevoked(true));
		refreshTokenRepository.saveAll(validUserTokens);
	}

	private AuthResponse mapToDto(Long userId, String accessToken, String refreshToken) {
		return AuthResponse.builder()
				.userId(userId)
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}

}
