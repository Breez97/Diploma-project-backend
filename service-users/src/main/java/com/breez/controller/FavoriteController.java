package com.breez.controller;

import com.breez.dto.Response;
import com.breez.dto.request.AddFavoriteRequest;
import com.breez.dto.response.FavoriteItemResponse;
import com.breez.security.CustomUserDetails;
import com.breez.service.FavoriteService;
import com.breez.service.ValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/favorite")
@RequiredArgsConstructor
public class FavoriteController {

	private static final Logger logger = LoggerFactory.getLogger(FavoriteController.class);

	private final FavoriteService favoriteService;
	private final ValidationService validationService;

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Response<List<FavoriteItemResponse>>> getUserFavorites(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		validationService.validateHeaders(sessionId, logger);
		List<FavoriteItemResponse> favorites = favoriteService.getFavorites(userDetails.getId());
		return ResponseEntity.ok(Response.success(favorites, "Favorites retrieved successfully"));
	}

	@PostMapping("/add")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Response<FavoriteItemResponse>> addFavorite(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@Valid @RequestBody AddFavoriteRequest request) {
		validationService.validateHeaders(sessionId, logger);
		FavoriteItemResponse addedFavorite = favoriteService.addFavorite(userDetails.getId(), request);
		return ResponseEntity.status(HttpStatus.CREATED).body(Response.success(addedFavorite, "Item added successfully"));
	}

	@GetMapping("/delete/{itemId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Response<Void>> removeFavorite(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable Long itemId) {
		validationService.validateHeaders(sessionId, logger);
		favoriteService.removeFavorite(userDetails.getId(), itemId);
		return ResponseEntity.ok(Response.success("Item removed successfully"));
	}

}
