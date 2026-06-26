package com.dev.simplespa.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import com.dev.simplespa.model.domain.LoginEvent;
import com.dev.simplespa.model.domain.LoginStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

class LoginEventDaoTest {

	private NamedParameterJdbcTemplate jdbcTemplate;
	private LoginEventDao loginEventDao;

	@BeforeEach
	void setUp() {
		jdbcTemplate = mock(NamedParameterJdbcTemplate.class);
		loginEventDao = new LoginEventDao(jdbcTemplate);
	}

	@Test
	void create_shouldInsertAndReturnLoginEventWithGeneratedId() {
		LoginEvent input = LoginEvent.builder()
			.userId(1)
			.username("user1")
			.status(LoginStatus.SUCCESS)
			.ipAddress("127.0.0.1")
			.userAgent("JUnit")
			.createdBy("user1")
			.build();

		ArgumentCaptor<KeyHolder> keyHolderCaptor = ArgumentCaptor.forClass(KeyHolder.class);

		when(jdbcTemplate.update(anyString(), any(SqlParameterSource.class), keyHolderCaptor.capture()))
			.thenAnswer(invocation -> {
				GeneratedKeyHolder kh = (GeneratedKeyHolder) keyHolderCaptor.getValue();
				kh.getKeyList().add(Map.of("GENERATED_KEY", 42));
				return 1;
			});

		LoginEvent result = loginEventDao.create(input);

		assertThat(result.getId()).isEqualTo(42);
		assertThat(result.getUsername()).isEqualTo("user1");
		assertThat(result.getStatus()).isEqualTo(LoginStatus.SUCCESS);
		assertThat(result.getEventDate()).isNotNull();

		verify(jdbcTemplate).update(
			contains("INSERT INTO LOGIN_EVENT"),
			any(SqlParameterSource.class),
			any(KeyHolder.class)
		);
	}

	@Test
	void create_shouldThrowWhenNoGeneratedKeyReturned() {
		LoginEvent input = LoginEvent.builder()
			.username("user1")
			.status(LoginStatus.SUCCESS)
			.createdBy("user1")
			.build();

		when(jdbcTemplate.update(anyString(), any(), any())).thenReturn(1);

		assertThatThrownBy(() -> loginEventDao.create(input))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Failed to retrieve generated ID");
	}
}
