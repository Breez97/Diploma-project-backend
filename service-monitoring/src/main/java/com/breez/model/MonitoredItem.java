package com.breez.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "monitored_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitoredItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String email;

	@Column(name = "itemId", nullable = false)
	private Long itemId;

	@Column(name = "marketplace_source", nullable = false)
	private String marketplaceSource;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "monitoredItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@OrderBy("timestamp DESC")
	private List<PriceHistoryEntry> priceHistory;

}
