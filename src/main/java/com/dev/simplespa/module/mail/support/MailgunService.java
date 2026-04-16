package com.dev.simplespa.module.mail.support;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.model.message.Message;
import com.mailgun.model.message.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailgunService {

	private final MailgunConfig mailgunConfig;
	private final MailgunMessagesApi mailgunMessagesApi;

	public MessageResponse sendMessage(Message message) {
		return this.mailgunMessagesApi.sendMessage(this.mailgunConfig.domain(), message);
	}

}
