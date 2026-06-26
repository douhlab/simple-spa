package com.dev.simplespa.model.domain;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class LoginEvent {
	private int id;
	private Integer userId;
	private String username;
	private LoginStatus status;
	private String failureReason;
	private String ipAddress;
	private String userAgent;
	private LocalDateTime eventDate;
	private String createdBy;
}
