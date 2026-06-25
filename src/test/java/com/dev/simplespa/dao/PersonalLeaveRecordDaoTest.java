package com.dev.simplespa.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.dev.simplespa.model.domain.Employer;
import com.dev.simplespa.model.domain.PersonalLeaveRecord;
import com.dev.simplespa.component.controller.dto.PersonalLeaveRecordCreationRequest;
import com.dev.simplespa.model.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

class PersonalLeaveRecordDaoTest {

	private NamedParameterJdbcTemplate jdbcTemplate;
	private PersonalLeaveRecordDao personalLeaveRecordDao;

	@BeforeEach
	void setUp() {
		jdbcTemplate = mock(NamedParameterJdbcTemplate.class);
		personalLeaveRecordDao = new PersonalLeaveRecordDao(jdbcTemplate);
	}

	@Test
	void create_shouldInsertAndReturnUserWithGeneratedId() {
		// Given
		var now = LocalDateTime.now();
		PersonalLeaveRecordCreationRequest input = PersonalLeaveRecordCreationRequest.builder()
			.userId(1)
			.employerId(2)
			.dateHappened(now.toLocalDate())
			.numberOfDays(5)
			.description("Test leave")
			.currentBalance(10d)
			.note("Test note")
			.build();

		ArgumentCaptor<KeyHolder> keyHolderCaptor = ArgumentCaptor.forClass(KeyHolder.class);

		// Mock jdbcTemplate.update to simulate DB generated ID
		when(jdbcTemplate.update(
			anyString(),
			any(SqlParameterSource.class),
			keyHolderCaptor.capture()
		)).thenAnswer(invocation -> {
			KeyHolder kh = keyHolderCaptor.getValue();
			((GeneratedKeyHolder) kh).getKeyList()
				.add(Map.of("GENERATED_KEY", 42));
			return 1;
		});

		// When
		PersonalLeaveRecord result = personalLeaveRecordDao.create(input);

		// Then
		assertThat(result.getId()).isEqualTo(42);
		assertThat(result.getDescription()).isEqualTo("Test leave");
		assertThat(result.getCreatedBy()).isEqualTo("createdByMe");
		assertThat(result.getCreationDate()).isNotNull();

		verify(jdbcTemplate).update(
			contains("INSERT INTO PERSONAL_LEAVE_RECORD"),
			any(SqlParameterSource.class),
			any(KeyHolder.class)
		);
	}

	@Test
	void create_shouldThrowWhenNoGeneratedKeyReturned() {

		var now = LocalDateTime.now();
		PersonalLeaveRecordCreationRequest input = PersonalLeaveRecordCreationRequest.builder()
			.userId(1)
			.employerId(2)
			.dateHappened(now.toLocalDate())
			.numberOfDays(5)
			.description("Test leave")
			.currentBalance(10d)
			.note("Test note")
			.build();

		when(jdbcTemplate.update(anyString(), any(), any()))
			.thenReturn(1);

		assertThatThrownBy(() -> personalLeaveRecordDao.create(input))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Failed to retrieve generated ID");
	}

	@Test
	void constructorShouldSetJdbcTemplate() {
		assertThat(personalLeaveRecordDao).isNotNull();
	}

	@Test
	void findAllShouldReturnRecordsForUser() {
		var now = LocalDateTime.now();
		PersonalLeaveRecord expected = PersonalLeaveRecord.builder()
			.id(1)
			.user(User.builder().id(42).build())
			.description("leave")
			.dateHappened(now.toLocalDate())
			.build();

		when(jdbcTemplate.query(anyString(),
			any(MapSqlParameterSource.class),
			any(PersonalLeaveRecordDao.PersonalLeaveRecordRowMapper.class)))
			.thenReturn(List.of(expected));

		List<PersonalLeaveRecord> result = personalLeaveRecordDao.findAll(42);

		assertThat(result).containsExactly(expected);

		ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
		verify(jdbcTemplate).query(anyString(), captor.capture(),
			any(PersonalLeaveRecordDao.PersonalLeaveRecordRowMapper.class));
		assertThat(captor.getValue().getValue("userId")).isEqualTo(42);
	}

	@Test
	void findByIdShouldReturnOptionalWhenFound() {
		var now = LocalDateTime.now();
		PersonalLeaveRecord expected = PersonalLeaveRecord.builder()
			.user(User.builder().id(1).build())
			.employer(Employer.builder().id(2).build())
			.dateHappened(now.toLocalDate())
			.numberOfDays(5)
			.description("Test leave")
			.currentBalance(10)
			.note("Test note")
			.createdBy("lab")
			.build();

		// Mock jdbcTemplate.query to return a list with one MyFirst
		when(jdbcTemplate.query(anyString(),
			any(MapSqlParameterSource.class),
			any(PersonalLeaveRecordDao.PersonalLeaveRecordRowMapper.class)))
			.thenReturn(Collections.singletonList(expected));

		Optional<PersonalLeaveRecord> result = personalLeaveRecordDao.findById(1);

		assertThat(result).isPresent();
		assertThat(result.get()).isEqualTo(expected);

		// verify query was called with correct parameter
		ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
		verify(jdbcTemplate).query(anyString(),
			captor.capture(),
			any(PersonalLeaveRecordDao.PersonalLeaveRecordRowMapper.class));
		assertThat(captor.getValue().getValue("id")).isEqualTo(1);
	}

	@Test
	void findByIdShouldReturnEmptyWhenNotFound() {
		when(jdbcTemplate.query(anyString(),
			any(MapSqlParameterSource.class),
			any(PersonalLeaveRecordDao.PersonalLeaveRecordRowMapper.class)))
			.thenReturn(Collections.emptyList());

		Optional<PersonalLeaveRecord> result = personalLeaveRecordDao.findById(1);
		assertThat(result).isEmpty();
	}

	@Test
	void personalLeaveRecordRowMapperShouldMapResultSet() throws Exception {
		ResultSet rs = mock(ResultSet.class);
		when(rs.getInt("ID")).thenReturn(42);
		when(rs.getString("DESCRIPTION")).thenReturn("description");
		when(rs.getString("CREATED_BY")).thenReturn("me");
		when(rs.getTimestamp("CREATION_DATE")).thenReturn(Timestamp.valueOf(LocalDateTime.of(2026, 2, 12, 12, 0)));

		PersonalLeaveRecordDao.PersonalLeaveRecordRowMapper mapper =
			new PersonalLeaveRecordDao.PersonalLeaveRecordRowMapper();
		PersonalLeaveRecord result = mapper.mapRow(rs, 0);

		assertThat(result.getId()).isEqualTo(42);
		assertThat(result.getDescription()).isEqualTo("description");
		assertThat(result.getCreatedBy()).isEqualTo("me");
		assertThat(result.getCreationDate()).isEqualTo(LocalDateTime.of(2026, 2, 12, 12, 0));
	}

	@Test
	void shouldCallJdbcTemplateUpdate_withCorrectSqlAndParams() {
		int id = 123;

		// capture arguments
		ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SqlParameterSource> paramCaptor = ArgumentCaptor.forClass(SqlParameterSource.class);

		personalLeaveRecordDao.deleteById(id);

		verify(jdbcTemplate).update(sqlCaptor.capture(), paramCaptor.capture());

		// ✅ assert SQL
		assertEquals(
			"delete from PERSONAL_LEAVE_RECORD where ID = :id",
			sqlCaptor.getValue()
		);

		// ✅ assert parameter value
		SqlParameterSource params = paramCaptor.getValue();
		assertEquals(id, params.getValue("id"));
	}

}

