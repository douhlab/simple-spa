package com.dev.simplespa.testing.ondemand;

import java.time.LocalDate;
import java.util.List;

import com.dev.simplespa.module.mail.MailConfig;
import com.dev.simplespa.module.mail.MailService;
import com.dev.simplespa.module.mail.support.MailgunConfig;
import com.dev.simplespa.module.mail.support.MailgunService;
import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MailServiceManualTest {
	private MailService mailService;

	@BeforeEach
	public void setup() {
		MailConfig mailConfig =
			new MailConfig(List.of("haythamdouaihy@gmail.com"),
				"noreply@mysimplelab.com",
				"Manual test - Alert - I am simple-spa",
				"templates/mail/simpleMailTemplate.vm",
				List.of("DEV-simple-spa"));

		MailgunConfig mailgunConfig =
			new MailgunConfig("https://api.mailgun.net",
				"f396ca9df1bac43246f786b3c44bfb11-f9517a64-cc434e88", //todo inject secret here
				"sandbox5cb400a23ad34a679a16bc1a20039daa.mailgun.org");

		MailgunMessagesApi mailgunMessagesApi = MailgunClient.config(mailgunConfig.baseUrl(), mailgunConfig.apiKey())
			.createAsyncApi(MailgunMessagesApi.class);
		MailgunService mailgunService = new MailgunService(mailgunConfig, mailgunMessagesApi);
		mailService = new MailService(mailgunService, mailConfig);
	}

	@Test
	public void testSendBoxEstimationFallbackCalledAlertEmail() {

		String message1 = "hello1";
		String message2 = "hello2";
		String message3 = "hello3";

		var messages = List.of(message1, message2, message3);
		var date = LocalDate.now();

		mailService.sendGenericMail(date, messages);
	}
}
