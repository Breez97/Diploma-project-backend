package com.breez.service;

import com.breez.dto.request.AddFavoriteRequest;
import com.breez.dto.response.FavoriteItemResponse;

import java.util.List;

public interface FavoriteService {

	List<FavoriteItemResponse> getFavorites(Long userId);

	FavoriteItemResponse addFavorite(Long userId, AddFavoriteRequest request);

	void removeFavorite(Long userId, Long itemId);

}
