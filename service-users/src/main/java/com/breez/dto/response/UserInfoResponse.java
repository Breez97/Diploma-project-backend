package com.breez.dto.response;

import com.breez.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

	private Long id;
	private String email;
	private String firstName;
	private String lastName;
	private String avatarUrl;
	private Role role;
	private boolean enabled;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

}
