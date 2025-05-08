package com.breez.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_search_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "search", nullable = false)
	private String search;

	@Column(name = "added_at", nullable = false, updatable = false)
	@Builder.Default
	private LocalDateTime addedAt = LocalDateTime.now();

}
