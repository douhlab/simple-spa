package com.dev.simplespa.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.dev.simplespa.model.domain.Employer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

class EmployerDaoTest {

	private NamedParameterJdbcTemplate jdbcTemplate;
	private EmployerDao employerDao;

	@BeforeEach
	void setUp() {
		jdbcTemplate = mock(NamedParameterJdbcTemplate.class);
		employerDao = new EmployerDao(jdbcTemplate);
	}


	@Test
	void create_shouldInsertAndReturnEmployerEmployerWithGeneratedId() {
		// Given
		Employer input = Employer.builder()
			.name("Test employer")
			.createdBy("lab")
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
		Employer result = employerDao.create(input);

		// Then
		assertThat(result.getId()).isEqualTo(42);
		assertThat(result.getName()).isEqualTo("Test employer");
		assertThat(result.getCreatedBy()).isEqualTo("lab");
		assertThat(result.getCreationDate()).isNotNull();

		verify(jdbcTemplate).update(
			contains("INSERT INTO EMPLOYER"),
			any(SqlParameterSource.class),
			any(KeyHolder.class)
		);
	}

	@Test
	void create_shouldThrowWhenNoGeneratedKeyReturned() {

		Employer input = Employer.builder()
			.name("Test employer")
			.createdBy("lab")
			.build();

		when(jdbcTemplate.update(anyString(), any(), any()))
			.thenReturn(1);

		assertThatThrownBy(() -> employerDao.create(input))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Failed to retrieve generated ID");
	}

	@Test
	void constructorShouldSetJdbcTemplate() {
		assertThat(employerDao).isNotNull();
	}

	@Test
	void findByIdShouldReturnOptionalWhenFound() {
		Employer expected = Employer.builder()
			.id(1)
			.name("desc")
			.startWorkDate(LocalDate.now())
			.endWorkDate(LocalDate.now().plusDays(1))
			.build();

		// Mock jdbcTemplate.query to return a list with one MyFirst
		when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(EmployerDao.EmployerRowMapper.class)))
			.thenReturn(Collections.singletonList(expected));

		Optional<Employer> result = employerDao.findById(1);

		assertThat(result).isPresent();
		assertThat(result.get()).isEqualTo(expected);

		// verify query was called with correct parameter
		ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
		verify(jdbcTemplate).query(anyString(), captor.capture(), any(EmployerDao.EmployerRowMapper.class));
		assertThat(captor.getValue().getValue("id")).isEqualTo(1);
	}

	@Test
	void findByIdShouldReturnEmptyWhenNotFound() {
		when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(EmployerDao.EmployerRowMapper.class)))
			.thenReturn(Collections.emptyList());

		Optional<Employer> result = employerDao.findById(1);
		assertThat(result).isEmpty();
	}

	@Test
	void employerRowMapperShouldMapResultSet() throws Exception {
		ResultSet rs = mock(ResultSet.class);
		when(rs.getInt("ID")).thenReturn(42);
		when(rs.getString("NAME")).thenReturn("desc");
		when(rs.getDate("START_WORK_DATE")).thenReturn(Date.valueOf(LocalDate.of(2026, 2, 12)));
		when(rs.getDate("END_WORK_DATE")).thenReturn(Date.valueOf(LocalDate.of(2026, 2, 13)));
		when(rs.getString("CREATED_BY")).thenReturn("me");
		when(rs.getTimestamp("CREATION_DATE")).thenReturn(Timestamp.valueOf(LocalDateTime.of(2026, 2, 12, 12, 0)));

		EmployerDao.EmployerRowMapper mapper = new EmployerDao.EmployerRowMapper();
		Employer result = mapper.mapRow(rs, 0);

		assertThat(result.getId()).isEqualTo(42);
		assertThat(result.getName()).isEqualTo("desc");
		assertThat(result.getStartWorkDate()).isEqualTo(LocalDate.of(2026, 2, 12));
		assertThat(result.getEndWorkDate()).isEqualTo(LocalDate.of(2026, 2, 13));
		assertThat(result.getCreatedBy()).isEqualTo("me");
		assertThat(result.getCreationDate()).isEqualTo(LocalDateTime.of(2026, 2, 12, 12, 0));
	}
}
