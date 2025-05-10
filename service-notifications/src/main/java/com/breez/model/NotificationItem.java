package com.breez.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String email;

	@Column(name = "item_id", nullable = false)
	private Long itemId;

	@Column(name = "marketplace_source", nullable = false)
	private String marketplaceSource;

	@Column(name = "notifications_enabled", nullable = false)
	@Builder.Default
	private boolean notificationsEnabled = false;

}
