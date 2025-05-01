package com.breez.controller;

import com.breez.dto.GenericResponse;
import com.breez.dto.request.AddFavoriteRequest;
import com.breez.dto.response.FavoriteItemResponse;
import com.breez.exception.favorite.FavoriteAlreadyExistsException;
import com.breez.exception.favorite.ResourceNotFoundException;
import com.breez.security.CustomUserDetails;
import com.breez.service.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

	private final FavoriteService favoriteService;

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<GenericResponse<List<FavoriteItemResponse>>> getUserFavorites(@AuthenticationPrincipal CustomUserDetails userDetails) {
		List<FavoriteItemResponse> favorites = favoriteService.getFavorites(userDetails.getId());
		return ResponseEntity.ok(GenericResponse.success(favorites, "Favorites retrieved successfully"));
	}

	@PostMapping("/add")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<GenericResponse<FavoriteItemResponse>> addFavorite(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@Valid @RequestBody AddFavoriteRequest request) {
		try {
			FavoriteItemResponse addedFavorite = favoriteService.addFavorite(userDetails.getId(), request);
			return ResponseEntity.status(HttpStatus.CREATED).body(GenericResponse.success(addedFavorite, "Item added successfully"));
		} catch (FavoriteAlreadyExistsException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(GenericResponse.error(e.getMessage()));
		} catch (ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(GenericResponse.error(e.getMessage()));
		}
	}

	@GetMapping("/delete/{itemId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<GenericResponse<Void>> removeFavorite(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable Long itemId) {
		try {
			favoriteService.removeFavorite(userDetails.getId(), itemId);
			return ResponseEntity.ok(GenericResponse.success("Item removed successfully"));
		} catch (ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(GenericResponse.error(e.getMessage()));
		}
	}

}
