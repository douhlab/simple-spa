package com.dev.simplespa.module.mail;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("mail.alert.lab")
public record MailConfig(List<String> to, String from, String subject, String htmlTemplate, List<String> tags) {
}
