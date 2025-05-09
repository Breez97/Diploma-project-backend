package com.breez.controller;

import com.breez.dto.Response;
import com.breez.dto.request.AddFavoritesRequest;
import com.breez.dto.request.RemoveFavoritesRequest;
import com.breez.dto.response.FavoritesItemResponse;
import com.breez.security.CustomUserDetails;
import com.breez.service.FavoritesService;
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
@RequestMapping("/api/v1/users/favorites")
@RequiredArgsConstructor
public class FavoritesController {

	private static final Logger logger = LoggerFactory.getLogger(FavoritesController.class);

	private final FavoritesService favoriteService;
	private final ValidationService validationService;

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Response<List<FavoritesItemResponse>>> getUserFavorites(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		validationService.validateHeaders(sessionId, logger);
		List<FavoritesItemResponse> favorites = favoriteService.getFavorites(userDetails.getId());
		return ResponseEntity.ok(Response.success(favorites, "Favorites retrieved successfully"));
	}

	@PostMapping("/add")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Response<FavoritesItemResponse>> addFavorite(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@Valid @RequestBody AddFavoritesRequest request) {
		validationService.validateHeaders(sessionId, logger);
		FavoritesItemResponse addedFavorite = favoriteService.addFavorite(userDetails.getId(), request);
		return ResponseEntity.status(HttpStatus.CREATED).body(Response.success(addedFavorite, "Item added successfully"));
	}

	@PostMapping("/remove")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Response<Void>> removeFavorite(
			@RequestHeader(value = "Session-Id", required = false) String sessionId,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@Valid @RequestBody RemoveFavoritesRequest request) {
		validationService.validateHeaders(sessionId, logger);
		favoriteService.removeFavorite(userDetails.getId(), request);
		return ResponseEntity.ok(Response.success("Item removed successfully"));
	}

}
