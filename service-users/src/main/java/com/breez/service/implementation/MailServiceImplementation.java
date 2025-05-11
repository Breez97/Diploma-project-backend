package com.breez.service.implementation;

import com.breez.entity.Mail;
import com.breez.service.MailService;
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

	private final TemplateEngine templateEngine;
	private final JavaMailSender mailSender;

	@Override
	@Async
	public void sendEmailWithThymeleaf(Mail mail) throws MailException, MessagingException {
		Context context = new Context();

		String receiver = mail.getReceiver();
		String code = mail.getCode();
		context.setVariable("email", receiver);
		context.setVariable("code", code);
		String process = templateEngine.process("VerificationMail", context);
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
		helper.setSubject(mail.getSubject());
		helper.setFrom("SearchScope");
		helper.setText(process, true);
		helper.setTo(receiver);

		mailSender.send(message);
	}

}
