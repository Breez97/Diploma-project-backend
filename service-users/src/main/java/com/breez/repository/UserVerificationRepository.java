package com.breez.repository;

import com.breez.model.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserVerificationRepository extends JpaRepository<UserVerification, Long> {

	Optional<UserVerification> findByUserIdAndCode(Long userId, Long code);

}
