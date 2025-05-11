package com.breez.repository;

import com.breez.model.UserSearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSearchHistoryRepository extends JpaRepository<UserSearchHistory, Long> {

	List<UserSearchHistory> findByUserIdOrderByAddedAtDesc(Long userId);

	long countByUserId(Long userId);

	Optional<UserSearchHistory> findFirstByUserIdOrderByAddedAtAsc(Long userId);

	void deleteByUserId(Long userId);

}
