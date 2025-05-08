package com.breez.repository;

import com.breez.model.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {

	List<UserFavorite> findByUserId(Long userId);

	boolean existsByUserIdAndItemId(Long userId, Long itemId);

	void deleteByUserIdAndItemId(Long userId, Long itemId);

}
