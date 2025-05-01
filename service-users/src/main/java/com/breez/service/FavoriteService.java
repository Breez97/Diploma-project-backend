package com.breez.service;

import com.breez.dto.request.AddFavoriteRequest;
import com.breez.dto.response.FavoriteItemResponse;
import com.breez.exception.favorite.FavoriteAlreadyExistsException;
import com.breez.exception.favorite.ResourceNotFoundException;

import java.util.List;

public interface FavoriteService {

	FavoriteItemResponse addFavorite(Long userId, AddFavoriteRequest request) throws FavoriteAlreadyExistsException, ResourceNotFoundException;

	void removeFavorite(Long userId, Long itemId) throws ResourceNotFoundException;

	List<FavoriteItemResponse> getFavorites(Long userId);

}
