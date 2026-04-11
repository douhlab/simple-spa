package com.dev.simplespa.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dev.simplespa.model.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

class UserDaoTest {

	private NamedParameterJdbcTemplate jdbcTemplate;
	private UserDao userDao;

	@BeforeEach
	void setUp() {
		jdbcTemplate = mock(NamedParameterJdbcTemplate.class);
		userDao = new UserDao(jdbcTemplate);
	}

	@Test
	void register_shouldInsertAndReturnUserWithGeneratedId() {
		User input = User.builder()
			.username("user1")
			.email("test@mail.com")
			.passwordHash("hash")
			.name("John")
			.lastName("Doe")
			.phoneNumber("12345678")
			.build();

		ArgumentCaptor<KeyHolder> keyHolderCaptor = ArgumentCaptor.forClass(KeyHolder.class);

		when(jdbcTemplate.update(
			anyString(),
			any(SqlParameterSource.class),
			keyHolderCaptor.capture()
		)).thenAnswer(invocation -> {
			GeneratedKeyHolder kh = (GeneratedKeyHolder) keyHolderCaptor.getValue();
			kh.getKeyList().add(Map.of("ID", 42)); // IMPORTANT
			return 1;
		});

		User result = userDao.register(input);

		assertThat(result.getId()).isEqualTo(42);
		assertThat(result.getUsername()).isEqualTo("user1");
		assertThat(result.getCreationDate()).isNotNull();

		verify(jdbcTemplate).update(
			contains("INSERT INTO USERS"),
			any(SqlParameterSource.class),
			any(KeyHolder.class)
		);
	}

	@Test
	void register_shouldThrowWhenNoGeneratedKeyReturned() {

		User input = User.builder()
			.username("user1")
			.email("test@mail.com")
			.passwordHash("hash")
			.name("John")
			.lastName("Doe")
			.phoneNumber("12345678")
			.build();

		when(jdbcTemplate.update(anyString(), any(), any()))
			.thenReturn(1);

		assertThatThrownBy(() -> userDao.register(input))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Failed to retrieve generated ID");
	}

	@Test
	void constructorShouldSetJdbcTemplate() {
		assertThat(userDao).isNotNull();
	}

	@Test
	void findByIdShouldReturnOptionalWhenFound() {
		User expected = User.builder()
			.id(1)
			.name("name")
			.creationDate(LocalDateTime.now())
			.createdBy("me")
			.build();

		// Mock jdbcTemplate.query to return a list with one MyFirst
		when(jdbcTemplate.query(anyString(), any(SqlParameterSource.class), any(UserDao.UserRowMapper.class)))
			.thenReturn(Collections.singletonList(expected));

		Optional<User> result = userDao.findById(1);

		assertThat(result).isPresent();
		assertThat(result.get()).isEqualTo(expected);

		// verify query was called with correct parameter
		ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
		verify(jdbcTemplate).query(anyString(), captor.capture(), any(UserDao.UserRowMapper.class));
		assertThat(captor.getValue().getValue("id")).isEqualTo(1);
	}

	@Test
	void findByIdShouldReturnEmptyWhenNotFound() {
		when(jdbcTemplate.query(anyString(), any(SqlParameterSource.class), any(UserDao.UserRowMapper.class)))
			.thenReturn(Collections.emptyList());

		Optional<User> result = userDao.findById(1);
		assertThat(result).isEmpty();
	}

	@Test
	void userRowMapperShouldMapResultSet() throws Exception {
		ResultSet rs = mock(ResultSet.class);

		when(rs.getInt("ID")).thenReturn(42);
		when(rs.getString("USERNAME")).thenReturn("user1");
		when(rs.getString("EMAIL")).thenReturn("test@mail.com");
		when(rs.getString("PASSWORD_HASH")).thenReturn("hash");
		when(rs.getString("NAME")).thenReturn("John");
		when(rs.getString("LAST_NAME")).thenReturn("Doe");
		when(rs.getString("PHONE_NUMBER")).thenReturn("12345678");
		when(rs.getString("CREATED_BY")).thenReturn("me");
		when(rs.getTimestamp("CREATION_DATE"))
			.thenReturn(Timestamp.valueOf(LocalDateTime.of(2026, 2, 12, 12, 0)));

		UserDao.UserRowMapper mapper = new UserDao.UserRowMapper();
		User result = mapper.mapRow(rs, 0);

		assertThat(result.getId()).isEqualTo(42);
		assertThat(result.getUsername()).isEqualTo("user1");
		assertThat(result.getEmail()).isEqualTo("test@mail.com");
		assertThat(result.getName()).isEqualTo("John");
		assertThat(result.getLastName()).isEqualTo("Doe");
		assertThat(result.getPhoneNumber()).isEqualTo("12345678");
		assertThat(result.getCreatedBy()).isEqualTo("me");
	}
}

