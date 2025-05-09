package com.breez.repository;

import com.breez.model.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {

	List<UserFavorite> findByUserId(Long userId);

	boolean existsByUserIdAndItemIdAndMarketplaceSource(Long userId, Long itemId, String marketplaceSource);

	void deleteByUserIdAndItemIdAndMarketplaceSource(Long userId, Long itemId, String marketplaceSource);

}
