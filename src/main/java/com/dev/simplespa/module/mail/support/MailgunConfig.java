package com.dev.simplespa.module.mail.support;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Builder
@ConfigurationProperties("mailgun")
public record MailgunConfig(@NotBlank String baseUrl, @NotNull String apiKey, String domain) {

}
