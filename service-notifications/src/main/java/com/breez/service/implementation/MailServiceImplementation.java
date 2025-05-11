package com.breez.service.implementation;

import com.breez.dto.event.PriceAlertEventDto;
import com.breez.entity.Mail;
import com.breez.service.MailService;
import com.breez.service.ValidationService;
import com.breez.util.CommonUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class MailServiceImplementation implements MailService {

	private final CommonUtil commonUtil;
	private final TemplateEngine templateEngine;
	private final JavaMailSender mailSender;
	private final ValidationService validationService;

	@Override
	@Async
	public void sendEmailWithThymeleaf(Mail mail) throws MailException, MessagingException {
		Context context = new Context();
		PriceAlertEventDto event = validationService.validatePriceAlertEvent(mail.getEvent());

		String receiver = mail.getReceiver();
		context.setVariable("productName", commonUtil.capitalizeFirstLetter(event.getProductName()));
		context.setVariable("productImageUrl", event.getProductImageUrl());
		context.setVariable("productUrl", event.getProductUrl());
		context.setVariable("oldPrice", event.getOldPrice());
		context.setVariable("newPrice", event.getNewPrice());
		context.setVariable("marketplaceSource", commonUtil.capitalizeFirstLetter(event.getMarketplaceSource()));
		String process = templateEngine.process("NotificationMail", context);
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
		helper.setSubject(mail.getSubject());
		helper.setFrom("SearchScope");
		helper.setText(process, true);
		helper.setTo(receiver);

		mailSender.send(message);
	}

}
