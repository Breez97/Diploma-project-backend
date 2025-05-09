package com.breez.service.implementation;

import com.breez.dto.request.AddFavoritesRequest;
import com.breez.dto.request.RemoveFavoritesRequest;
import com.breez.dto.response.FavoritesItemResponse;
import com.breez.exception.favorite.FavoriteAlreadyExistsException;
import com.breez.exception.UserNotFoundException;
import com.breez.exception.favorite.FavoriteNotFoundException;
import com.breez.model.User;
import com.breez.model.UserFavorite;
import com.breez.repository.UserFavoriteRepository;
import com.breez.repository.UserRepository;
import com.breez.service.FavoritesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoritesServiceImplementation implements FavoritesService {

	private final UserFavoriteRepository favoriteRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional(readOnly = true)
	public List<FavoritesItemResponse> getFavorites(Long userId) {
		List<UserFavorite> favorites = favoriteRepository.findByUserId(userId);
		return favorites.stream()
				.map(this::mapToDto)
				.toList();
	}

	@Override
	@Transactional
	public FavoritesItemResponse addFavorite(Long userId, AddFavoritesRequest request) {
		Long itemId = request.getItemId();
		String marketplaceSource = request.getMarketplaceSource();
		if (favoriteRepository.existsByUserIdAndItemIdAndMarketplaceSource(userId, itemId, marketplaceSource)) {
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
	public void removeFavorite(Long userId, RemoveFavoritesRequest request) {
		Long itemId = request.getItemId();
		String marketplaceSource = request.getMarketplaceSource();
		if (!favoriteRepository.existsByUserIdAndItemIdAndMarketplaceSource(userId, itemId, marketplaceSource)) {
			throw new FavoriteNotFoundException("Favorite item with ID " + itemId + " not found for this user in marketplace: " + marketplaceSource);
		}
		userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
		favoriteRepository.deleteByUserIdAndItemIdAndMarketplaceSource(userId, itemId, marketplaceSource);
	}

	private FavoritesItemResponse mapToDto(UserFavorite entity) {
		return FavoritesItemResponse.builder()
				.itemId(entity.getItemId())
				.marketplaceSource(entity.getMarketplaceSource())
				.addedAt(entity.getAddedAt())
				.build();
	}
}
