package com.dev.simplespa.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dev.simplespa.model.domain.MyFirst;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

class FirstDaoTest {

	private NamedParameterJdbcTemplate jdbcTemplate;
	private FirstDao firstDao;

	@BeforeEach
	void setUp() {
		jdbcTemplate = mock(NamedParameterJdbcTemplate.class);
		firstDao = new FirstDao(jdbcTemplate);
	}

	@Test
	void constructorShouldSetJdbcTemplate() {
		assertThat(firstDao).isNotNull();
	}

	@Test
	void findByIdShouldReturnOptionalWhenFound() throws Exception {
		MyFirst expected = MyFirst.builder()
			.id(1)
			.description("desc")
			.creationDate(LocalDateTime.now())
			.createdBy("me")
			.build();

		// Mock jdbcTemplate.query to return a list with one MyFirst
		when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(FirstDao.UserRowMapper.class)))
			.thenReturn(Collections.singletonList(expected));

		Optional<MyFirst> result = firstDao.findById(1);

		assertThat(result).isPresent();
		assertThat(result.get()).isEqualTo(expected);

		// verify query was called with correct parameter
		ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
		verify(jdbcTemplate).query(anyString(), captor.capture(), any(FirstDao.UserRowMapper.class));
		assertThat(captor.getValue().getValue("id")).isEqualTo(1);
	}

	@Test
	void findByIdShouldReturnEmptyWhenNotFound() throws Exception {
		when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(FirstDao.UserRowMapper.class)))
			.thenReturn(Collections.emptyList());

		Optional<MyFirst> result = firstDao.findById(1);
		assertThat(result).isEmpty();
	}

	@Test
	void userRowMapperShouldMapResultSet() throws Exception {
		ResultSet rs = mock(ResultSet.class);
		when(rs.getInt("ID")).thenReturn(42);
		when(rs.getString("DESCRIPTION")).thenReturn("desc");
		when(rs.getString("CREATED_BY")).thenReturn("me");
		when(rs.getTimestamp("CREATION_DATE")).thenReturn(Timestamp.valueOf(LocalDateTime.of(2026, 2, 12, 12, 0)));

		FirstDao.UserRowMapper mapper = new FirstDao.UserRowMapper();
		MyFirst result = mapper.mapRow(rs, 0);

		assertThat(result.getId()).isEqualTo(42);
		assertThat(result.getDescription()).isEqualTo("desc");
		assertThat(result.getCreatedBy()).isEqualTo("me");
		assertThat(result.getCreationDate()).isEqualTo(LocalDateTime.of(2026, 2, 12, 12, 0));
	}
}

