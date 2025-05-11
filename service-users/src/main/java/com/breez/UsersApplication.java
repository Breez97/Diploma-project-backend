package com.breez;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
		org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration.class,
		org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration.class
})
public class UsersApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsersApplication.class, args);
	}

}
