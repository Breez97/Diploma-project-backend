package com.breez.service.implementation;

import com.breez.dto.event.PriceAlertEventDto;
import com.breez.dto.ProductDetailsDto;
import com.breez.model.MonitoredItem;
import com.breez.model.PriceHistoryEntry;
import com.breez.repository.MonitoredItemRepository;
import com.breez.service.PriceUpdateScheduler;
import com.breez.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PriceUpdateSchedulerImplementation implements PriceUpdateScheduler {

	private static final Logger logger = LoggerFactory.getLogger(PriceUpdateSchedulerImplementation.class);

	private final MonitoredItemRepository monitoredItemRepository;
	private final ProductSearchService productSearchService;
	private final KafkaTemplate<String, PriceAlertEventDto> kafkaTemplate;

	@Value("${kafka.topic.user-price-alerts}")
	private String userPriceAlertsTopic;

	private static final int MAX_PRICE_HISTORY_ENTRIES = 5;

	@Override
	@Scheduled(cron = "0 0 0 * * ?") // каждый день 00:00
	// @Scheduled(fixedDelay = 10000) // 10 секунд
	@Transactional
	public void updatePricesAndNotify() {
		List<MonitoredItem> itemsToUpdate = monitoredItemRepository.findAll();
		for (MonitoredItem item : itemsToUpdate) {
			logger.info("Processing item: id={}, email={}, itemId={}, marketplace={}", item.getId(), item.getEmail(), item.getItemId(), item.getMarketplaceSource());
			ProductDetailsDto productDetails = productSearchService.fetchProductDetails(item.getItemId(), item.getMarketplaceSource());
			if (productDetails != null) {
				BigDecimal newPrice = productDetails.getPrice();
				Optional<PriceHistoryEntry> latestHistoryEntryOpt = item.getPriceHistory()
						.stream()
						.max(Comparator.comparing(PriceHistoryEntry::getTimestamp));

				BigDecimal oldPrice = latestHistoryEntryOpt.map(PriceHistoryEntry::getPrice).orElse(null);
				PriceHistoryEntry newHistoryEntry = PriceHistoryEntry.builder()
						.monitoredItem(item)
						.price(newPrice)
						.timestamp(LocalDateTime.now())
						.build();
				item.getPriceHistory().add(newHistoryEntry);

				while (item.getPriceHistory().size() > MAX_PRICE_HISTORY_ENTRIES) {
					item.getPriceHistory().remove(item.getPriceHistory().size() - 1);
				}
				monitoredItemRepository.save(item);

				if (oldPrice != null && newPrice.compareTo(oldPrice) < 0) {
					logger.info("Price dropped for item id {}. Old: {}, New: {}. Sending alert", item.getId(), oldPrice, newPrice);
					PriceAlertEventDto alertEvent = PriceAlertEventDto.builder()
							.email(item.getEmail())
							.itemId(item.getItemId())
							.marketplaceSource(item.getMarketplaceSource())
							.productName(productDetails.getTitle())
							.productImageUrl(productDetails.getImageUrl())
							.productUrl(productDetails.getExternalLink())
							.oldPrice(oldPrice)
							.newPrice(newPrice)
							.build();
					kafkaTemplate.send(userPriceAlertsTopic, item.getEmail(), alertEvent);
					logger.info("Sent price drop alert for item id {} to Kafka topic {}", item.getId(), userPriceAlertsTopic);
				}
			}
		}
	}

	@Override
	public void updatePriceForSpecificItem(MonitoredItem item) {
		ProductDetailsDto productDetails = productSearchService.fetchProductDetails(item.getItemId(), item.getMarketplaceSource());
		if (productDetails != null) {
			BigDecimal newPrice = productDetails.getPrice();
			PriceHistoryEntry newHistoryEntry = PriceHistoryEntry.builder()
					.monitoredItem(item)
					.price(newPrice)
					.timestamp(LocalDateTime.now())
					.build();
			item.getPriceHistory().add(newHistoryEntry);
			monitoredItemRepository.save(item);
		}
	}

}
