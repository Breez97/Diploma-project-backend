package com.breez.service.implementation;

import com.breez.dto.event.PriceAlertEventDto;
import com.breez.service.TestPriceAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TestPriceAlertServiceImplementation implements TestPriceAlertService {

	private final KafkaTemplate<String, PriceAlertEventDto> kafkaTemplate;

	@Value("${app.kafka.topic.user-price-alerts}")
	private String userPriceAlertsTopic;

	@Value("${test.notifications.email}")
	private String testEmail;

	private static final Long testItemId = 329357708L;
	private static final String testMarketplaceSource = "wildberries";
	private static final String testProductName = "Кроссовки летние дышащие спортивные тканевые";
	private static final String testProductImageUrl = "https://basket-20.wbbasket.ru/vol3293/part329357/329357708/images/big/1.webp";
	private static final String testProductUrl = "https://www.wildberries.ru/catalog/329357708/detail.aspx";
	private static final BigDecimal testOldPrice = new BigDecimal(2174);
	private static final BigDecimal testNewPrice = new BigDecimal(2000);

	@Override
	public void sendTestPriceAlertEvent() {
		PriceAlertEventDto alertEvent = PriceAlertEventDto.builder()
				.email(testEmail)
				.itemId(testItemId)
				.marketplaceSource(testMarketplaceSource)
				.productName(testProductName)
				.productImageUrl(testProductImageUrl)
				.productUrl(testProductUrl)
				.oldPrice(testOldPrice)
				.newPrice(testNewPrice)
				.build();
		kafkaTemplate.send(userPriceAlertsTopic, testEmail, alertEvent);
	}

}
