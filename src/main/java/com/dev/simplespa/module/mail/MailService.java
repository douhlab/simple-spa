package com.dev.simplespa.module.mail;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dev.simplespa.module.mail.support.MailServiceHelper;
import com.dev.simplespa.module.mail.support.MailgunService;
import com.mailgun.model.message.Message;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService implements IMailService {
    private static final String MAIL_PARAM_DATE = "currentDate";
    private static final String MESSAGES = "messages";

    private final MailgunService mailgunService;
    private final MailConfig mailConfig;

    @Override
    public boolean sendGenericMail(LocalDate currentDate, List<String> messages) {

        Set<String> messageSet = new HashSet<>(messages);

        Map<String, Object> mailTemplateParams = new HashMap<>();
        mailTemplateParams.put(MAIL_PARAM_DATE, currentDate);
        mailTemplateParams.put(MESSAGES, messageSet);

        List<String> prefixes = List.of("a", "b", "c");
        MailConfig configWithPrefixes = MailServiceHelper.configWithSubjectPrefixes(mailConfig, prefixes);

        return sendMail(configWithPrefixes, mailTemplateParams);
    }

    private boolean sendMail(MailConfig mailConfig, Map<String, ?> mailTemplateParams) {
        String htmlBody = MailServiceHelper.buildMailTemplate(mailConfig.htmlTemplate(), mailTemplateParams);

        var message = Message.builder()
            .from(mailConfig.from())
            .to(mailConfig.to())
            .subject(mailConfig.subject())
            .html(htmlBody)
            .tag(mailConfig.tags())
            .build();

        try {
            var messageResponse = mailgunService.sendMessage(message);
            log.info("Email for [{}] sent to MailGun queue: {}", mailConfig.to(), messageResponse);
            return true;
        } catch (FeignException e) {
            log.error("Mail sending failed for message {}", message, e);
            return false;
        }
    }
}

