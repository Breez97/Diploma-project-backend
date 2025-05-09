package com.breez.service;

import com.breez.dto.request.AddFavoritesRequest;
import com.breez.dto.request.RemoveFavoritesRequest;
import com.breez.dto.response.FavoritesItemResponse;

import java.util.List;

public interface FavoritesService {

	List<FavoritesItemResponse> getFavorites(Long userId);

	FavoritesItemResponse addFavorite(Long userId, AddFavoritesRequest request);

	void removeFavorite(Long userId, RemoveFavoritesRequest request);

}
