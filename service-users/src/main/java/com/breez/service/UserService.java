package com.breez.service;

import com.breez.dto.request.UpdateUserPasswordRequest;
import com.breez.dto.request.UpdateUserInfoRequest;
import com.breez.dto.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

	UserResponse getUserInfo(String email);

	UserResponse updateUser(String email, UpdateUserInfoRequest request);

	void updateUserPassword(String email, UpdateUserPasswordRequest request);

	void updateUserAvatar(String email, MultipartFile file);

}
