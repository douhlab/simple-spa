package com.dev.simplespa.module.auth;

import com.dev.simplespa.dao.LoginEventDao;
import com.dev.simplespa.model.domain.LoginEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginEventService {

	private final LoginEventDao loginEventDao;

	public void record(LoginEvent loginEvent) {
		try {
			loginEventDao.create(loginEvent);
		} catch (Exception ex) {
			log.error("Failed to persist login event for username={}", loginEvent.getUsername(), ex);
		}
	}
}
