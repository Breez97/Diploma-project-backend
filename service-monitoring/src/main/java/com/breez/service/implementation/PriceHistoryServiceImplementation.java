package com.breez.service.implementation;

import com.breez.dto.request.PriceRequest;
import com.breez.dto.response.PriceDataEntryDto;
import com.breez.dto.response.PriceHistoryResponse;
import com.breez.exception.NoItemFoundException;
import com.breez.model.MonitoredItem;
import com.breez.repository.MonitoredItemRepository;
import com.breez.service.PriceHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PriceHistoryServiceImplementation implements PriceHistoryService {

	private final MonitoredItemRepository monitoredItemRepository;

	@Override
	@Transactional(readOnly = true)
	public PriceHistoryResponse getPriceHistoryForMonitoredItem(PriceRequest request) {
		String email = request.getEmail();
		Long itemId = request.getItemId();
		String marketplaceSource = request.getMarketplaceSource();

		Optional<MonitoredItem> itemOpt = monitoredItemRepository.findByEmailAndItemIdAndMarketplaceSource(email, itemId, marketplaceSource);
		if (itemOpt.isPresent()) {
			MonitoredItem item = itemOpt.get();
			return convertToPriceHistoryItemResponse(item);
		}
		throw new NoItemFoundException("No item found");
	}

	private PriceHistoryResponse convertToPriceHistoryItemResponse(MonitoredItem item) {
		List<PriceDataEntryDto> priceDataEntries = item.getPriceHistory().stream()
				.map(priceHistoryEntry -> PriceDataEntryDto.builder()
						.price(priceHistoryEntry.getPrice())
						.timestamp(priceHistoryEntry.getTimestamp())
						.build())
				.toList();
		return PriceHistoryResponse.builder()
				.email(item.getEmail())
				.itemId(item.getItemId())
				.marketplaceSource(item.getMarketplaceSource())
				.priceHistory(priceDataEntries)
				.build();
	}

}
