package com.breez.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistoryEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "monitored_item_id", nullable = false)
	private MonitoredItem monitoredItem;

	@Column(nullable = false)
	private BigDecimal price;

	@Column(nullable = false, updatable = false)
	@Builder.Default
	private LocalDateTime timestamp = LocalDateTime.now();

}
