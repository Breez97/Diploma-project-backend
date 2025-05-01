package com.breez.repository;

import com.breez.model.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {

	List<UserFavorite> findByUserId(Long userId);

	Optional<UserFavorite> findByUserIdAndItemId(Long userId, Long itemId);

	boolean existsByUserIdAndItemId(Long userId, Long itemId);

	void deleteByUserIdAndItemId(Long userId, Long itemId);

}
