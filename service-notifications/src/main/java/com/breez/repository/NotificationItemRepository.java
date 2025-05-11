package com.breez.repository;

import com.breez.model.NotificationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationItemRepository extends JpaRepository<NotificationItem, Long> {

	Optional<NotificationItem> findByEmailAndItemIdAndMarketplaceSource(String email, Long itemId, String marketplaceSource);

	void deleteByEmailAndItemIdAndMarketplaceSource(String email, Long itemId, String marketplaceSource);

}
