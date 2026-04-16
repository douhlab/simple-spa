package com.dev.simplespa.component.context;

import com.dev.simplespa.module.mail.support.MailgunConfig;
import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailgunBeanConfig {

    @Bean
    public MailgunMessagesApi mailgunMessagesApi(MailgunConfig config) {
        return MailgunClient.config(config.apiKey())
            .createApi(MailgunMessagesApi.class);
    }
}
