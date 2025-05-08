package com.breez.service.implementation;

import com.breez.dto.request.AddSearchHistoryRequest;
import com.breez.dto.response.SearchHistoryResponse;
import com.breez.exception.SearchHistoryAlreadyExistException;
import com.breez.exception.UserNotFoundException;
import com.breez.model.User;
import com.breez.model.UserSearchHistory;
import com.breez.repository.UserRepository;
import com.breez.repository.UserSearchHistoryRepository;
import com.breez.service.SearchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.breez.constants.Constants.MAX_HISTORY_SIZE;

@Service
@RequiredArgsConstructor
public class SearchHistoryServiceImplementation implements SearchHistoryService {

	private final UserRepository userRepository;
	private final UserSearchHistoryRepository searchHistoryRepository;

	@Override
	@Transactional(readOnly = true)
	public List<SearchHistoryResponse> getSearchHistory(Long userId) {
		return searchHistoryRepository.findByUserIdOrderByAddedAtDesc(userId)
				.stream()
				.map(history -> new SearchHistoryResponse(history.getSearch()))
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public SearchHistoryResponse addSearchHistory(Long userId, AddSearchHistoryRequest request) {
		User userReference = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
		List<UserSearchHistory> searchHistory = searchHistoryRepository.findByUserIdOrderByAddedAtDesc(userId);
		List<String> searchValues = searchHistory.stream().map(UserSearchHistory::getSearch).toList();
		String currentSearch = request.getSearchHistory();
		for (String search : searchValues) {
			if (StringUtils.equals(search, currentSearch)) {
				throw new SearchHistoryAlreadyExistException("Current search request already exists");
			}
		}

		long currentHistoryCount = searchHistoryRepository.countByUserId(userId);
		if (currentHistoryCount >= MAX_HISTORY_SIZE) {
			searchHistoryRepository.findFirstByUserIdOrderByAddedAtAsc(userId).ifPresent(searchHistoryRepository::delete);
		}
		UserSearchHistory newSearchHistory = UserSearchHistory.builder()
				.user(userReference)
				.search(request.getSearchHistory())
				.build();
		UserSearchHistory savedEntry = searchHistoryRepository.save(newSearchHistory);
		return SearchHistoryResponse.builder().searchHistory(savedEntry.getSearch()).build();
	}

	@Override
	@Transactional
	public void clearSearchHistory(Long userId) {
		userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
		searchHistoryRepository.deleteByUserId(userId);
	}

}
