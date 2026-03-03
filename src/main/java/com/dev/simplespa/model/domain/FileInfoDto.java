package com.dev.simplespa.model.domain;

import java.time.Instant;

import lombok.Builder;

@Builder
public record FileInfoDto (
     String fileName, String contentType, long size, String url, Instant uploadedAt
) {
}
