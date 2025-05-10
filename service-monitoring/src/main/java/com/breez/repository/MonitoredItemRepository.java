package com.breez.repository;

import com.breez.model.MonitoredItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MonitoredItemRepository extends JpaRepository<MonitoredItem, Long> {

	Optional<MonitoredItem> findByEmailAndItemIdAndMarketplaceSource(String email, Long itemId, String marketplaceSource);

	void deleteByEmailAndItemIdAndMarketplaceSource(String email, Long itemId, String marketplaceSource);

}
