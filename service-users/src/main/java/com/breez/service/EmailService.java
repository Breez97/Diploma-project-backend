package com.breez.service;

import com.breez.entity.Mail;
import jakarta.mail.MessagingException;
import org.springframework.mail.MailException;

public interface EmailService {

	void sendEmailWithThymeleaf(Mail mail) throws MailException, MessagingException;

}
