package com.breez.repository;

import com.breez.model.RefreshToken;
import com.breez.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByToken(String token);

	List<RefreshToken> findByUserAndRevokedFalse(User user);

	@Transactional
	void deleteAllByUser(User user);

	@Transactional
	void deleteAllByIdIn(List<Long> ids);

	@Transactional
	void deleteByExpiryDateBefore(LocalDateTime now);

}
