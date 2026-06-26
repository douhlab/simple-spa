package com.dev.simplespa.module.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dev.simplespa.dao.LoginEventDao;
import com.dev.simplespa.model.domain.LoginEvent;
import com.dev.simplespa.model.domain.LoginStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginEventServiceTest {

	@Mock
	private LoginEventDao loginEventDao;

	@InjectMocks
	private LoginEventService loginEventService;

	@Test
	void record_shouldPersistLoginEvent() {
		var event = LoginEvent.builder()
			.username("user1")
			.status(LoginStatus.SUCCESS)
			.createdBy("user1")
			.build();

		when(loginEventDao.create(any(LoginEvent.class))).thenReturn(event);

		loginEventService.record(event);

		verify(loginEventDao).create(event);
	}

	@Test
	void record_shouldNotThrowWhenPersistenceFails() {
		var event = LoginEvent.builder()
			.username("user1")
			.status(LoginStatus.SUCCESS)
			.createdBy("user1")
			.build();

		doThrow(new RuntimeException("db down")).when(loginEventDao).create(any(LoginEvent.class));

		loginEventService.record(event);

		verify(loginEventDao).create(event);
	}
}
