package com.breez.repository;

import com.breez.model.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserVerificationRepository extends JpaRepository<UserVerification, Long> {

	Optional<UserVerification> findByUserId(Long userId);

	Optional<UserVerification> findByUserIdAndCode(Long userId, Long code);

	@Transactional
	void deleteByUserId(Long userId);

	@Transactional
	void deleteByCode(Long code);

}
