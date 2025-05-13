package com.breez.service.implementation;

import com.breez.dto.event.FavoritesEventDto;
import com.breez.dto.event.NotificationsEventDto;
import com.breez.dto.request.AddFavoritesRequest;
import com.breez.dto.request.RemoveFavoritesRequest;
import com.breez.dto.response.FavoritesItemResponse;
import com.breez.enums.ActionType;
import com.breez.enums.Notification;
import com.breez.exception.favorite.FavoriteAlreadyExistsException;
import com.breez.exception.UserNotFoundException;
import com.breez.exception.favorite.FavoriteNotFoundException;
import com.breez.model.User;
import com.breez.model.UserFavorite;
import com.breez.repository.UserFavoriteRepository;
import com.breez.repository.UserRepository;
import com.breez.service.FavoritesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoritesServiceImplementation implements FavoritesService {

	private static final Logger logger = LoggerFactory.getLogger(FavoritesServiceImplementation.class);

	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final UserFavoriteRepository userFavoriteRepository;
	private final UserRepository userRepository;

	@Value("${kafka.topic.user-favorites}")
	private String userFavoritesTopic;
	@Value("${kafka.topic.user-notifications}")
	private String userNotificationsTopic;

	@Autowired
	public FavoritesServiceImplementation(KafkaTemplate<String, Object> kafkaTemplate,
										  UserFavoriteRepository userFavoriteRepository,
										  UserRepository userRepository) {
		this.kafkaTemplate = kafkaTemplate;
		this.userFavoriteRepository = userFavoriteRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<FavoritesItemResponse> getFavorites(Long userId) {
		List<UserFavorite> favorites = userFavoriteRepository.findByUserId(userId);
		return favorites.stream()
				.map(this::mapToDto)
				.toList();
	}

	@Override
	@Transactional
	public FavoritesItemResponse addFavorite(Long userId, AddFavoritesRequest request) {
		Long itemId = request.getItemId();
		String marketplaceSource = request.getMarketplaceSource();
		if (userFavoriteRepository.existsByUserIdAndItemIdAndMarketplaceSource(userId, itemId, marketplaceSource)) {
			throw new FavoriteAlreadyExistsException("Item with ID " + request.getItemId() + " is already exists in favorites");
		}
		User userReference = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
		UserFavorite newFavorite = UserFavorite.builder()
				.user(userReference)
				.itemId(request.getItemId())
				.marketplaceSource(request.getMarketplaceSource())
				.build();

		UserFavorite savedFavorite = userFavoriteRepository.save(newFavorite);

		FavoritesEventDto favoriteEvent = FavoritesEventDto.builder()
				.email(userReference.getEmail())
				.itemId(itemId)
				.marketplaceSource(marketplaceSource)
				.action(ActionType.ADD)
				.build();
		kafkaTemplate.send(userFavoritesTopic, userReference.getEmail(), favoriteEvent);

		NotificationsEventDto notificationEvent = NotificationsEventDto.builder()
				.email(userReference.getEmail())
				.itemId(itemId)
				.marketplaceSource(marketplaceSource)
				.notification(Notification.DISABLE)
				.build();
		kafkaTemplate.send(userNotificationsTopic, userReference.getEmail(), notificationEvent);

		return mapToDto(savedFavorite);
	}

	@Override
	@Transactional
	public void removeFavorite(Long userId, RemoveFavoritesRequest request) {
		Long itemId = request.getItemId();
		String marketplaceSource = request.getMarketplaceSource();
		if (!userFavoriteRepository.existsByUserIdAndItemIdAndMarketplaceSource(userId, itemId, marketplaceSource)) {
			throw new FavoriteNotFoundException("Favorite item with ID " + itemId + " not found for this user in marketplace: " + marketplaceSource);
		}
		User userReference = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
		userFavoriteRepository.deleteByUserIdAndItemIdAndMarketplaceSource(userId, itemId, marketplaceSource);

		FavoritesEventDto favoriteEvent = FavoritesEventDto.builder()
				.email(userReference.getEmail())
				.itemId(itemId)
				.marketplaceSource(marketplaceSource)
				.action(ActionType.REMOVE)
				.build();
		kafkaTemplate.send(userFavoritesTopic, userReference.getEmail(), favoriteEvent);

		NotificationsEventDto notificationsEvent = NotificationsEventDto.builder()
				.email(userReference.getEmail())
				.itemId(itemId)
				.marketplaceSource(marketplaceSource)
				.notification(Notification.REMOVE)
				.build();
		kafkaTemplate.send(userNotificationsTopic, userReference.getEmail(), notificationsEvent);
	}

	private FavoritesItemResponse mapToDto(UserFavorite entity) {
		return FavoritesItemResponse.builder()
				.itemId(entity.getItemId())
				.marketplaceSource(entity.getMarketplaceSource())
				.addedAt(entity.getAddedAt())
				.build();
	}
}
