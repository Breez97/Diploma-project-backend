package com.breez.service.implementation;

import com.breez.dto.request.AddFavoriteRequest;
import com.breez.dto.response.FavoriteItemResponse;
import com.breez.exception.favorite.FavoriteAlreadyExistsException;
import com.breez.exception.UserNotFoundException;
import com.breez.model.User;
import com.breez.model.UserFavorite;
import com.breez.repository.UserFavoriteRepository;
import com.breez.repository.UserRepository;
import com.breez.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImplementation implements FavoriteService {

	private final UserFavoriteRepository favoriteRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional(readOnly = true)
	public List<FavoriteItemResponse> getFavorites(Long userId) {
		List<UserFavorite> favorites = favoriteRepository.findByUserId(userId);
		return favorites.stream()
				.map(this::mapToDto)
				.toList();
	}

	@Override
	@Transactional
	public FavoriteItemResponse addFavorite(Long userId, AddFavoriteRequest request) {
		if (favoriteRepository.existsByUserIdAndItemId(userId, request.getItemId())) {
			throw new FavoriteAlreadyExistsException("Item with ID " + request.getItemId() + " is already exists in favorites");
		}
		User userReference = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
		UserFavorite newFavorite = UserFavorite.builder()
				.user(userReference)
				.itemId(request.getItemId())
				.marketplaceSource(request.getMarketplaceSource())
				.build();

		UserFavorite savedFavorite = favoriteRepository.save(newFavorite);
		return mapToDto(savedFavorite);
	}

	@Override
	@Transactional
	public void removeFavorite(Long userId, Long itemId) {
		if (!favoriteRepository.existsByUserIdAndItemId(userId, itemId)) {
			throw new UserNotFoundException("Favorite item with ID " + itemId + " not found for this user.");
		}
		favoriteRepository.deleteByUserIdAndItemId(userId, itemId);
	}

	private FavoriteItemResponse mapToDto(UserFavorite entity) {
		return FavoriteItemResponse.builder()
				.itemId(entity.getItemId())
				.marketplaceSource(entity.getMarketplaceSource())
				.addedAt(entity.getAddedAt())
				.build();
	}
}
