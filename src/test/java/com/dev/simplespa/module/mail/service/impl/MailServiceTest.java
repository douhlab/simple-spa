package com.dev.simplespa.module.mail.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;

import com.dev.simplespa.module.mail.MailConfig;
import com.dev.simplespa.module.mail.MailService;
import com.dev.simplespa.module.mail.support.MailgunService;
import com.mailgun.model.message.Message;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {
	private MailService mailService;

	@Mock
	private MailgunService mailgunService;

	@BeforeEach
	public void setup() {
		var mailConfig =
			new MailConfig(List.of("to@a.b"),
				"from@a.b",
				"boxEstimationFallbackSubject",
				"templates/mail/alertSimpleSpa.vm",
				List.of("DEV-simple-spa"));

		this.mailService = new MailService(mailgunService, mailConfig);
	}

	@Test
	void testSendGenericMail() {
		// given

		// when
		var isMailSent = mailService.sendGenericMail(LocalDate.now(), List.of());

		// then
		var captor = ArgumentCaptor.forClass(Message.class);
		verify(mailgunService).sendMessage(captor.capture());
		assertThat(isMailSent).isTrue();
		assertThat(captor.getValue().getFrom()).isEqualTo("from@a.b");
		assertThat(captor.getValue().getTo()).containsExactly("to@a.b");
		assertThat(captor.getValue().getSubject()).isEqualTo(
			"[a, b, c] boxEstimationFallbackSubject");
		assertThat(captor.getValue().getTag()).containsExactly("DEV-simple-spa");
		assertThat(captor.getValue().getHtml()).contains(
			"Here are the number of messages to send");
	}

	@Test
	void testSendGenericMail_exception() {
		// given
		doThrow(FeignException.class).when(mailgunService).sendMessage(any());

		// when
		var isMailSent = mailService.sendGenericMail(LocalDate.now(), List.of());

		// then
		assertThat(isMailSent).isFalse();
	}
}
