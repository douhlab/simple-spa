package com.dev.simplespa.dao;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.dev.simplespa.model.domain.Employer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class EmployerDao {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public EmployerDao(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Employer create(Employer employer) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		var now = LocalDateTime.now();

		SqlParameterSource parameters = new MapSqlParameterSource()
			.addValue("name", employer.getName())
			.addValue("startWorkDate", employer.getStartWorkDate())
			.addValue("endWorkDate", employer.getEndWorkDate())
			.addValue("creationDate", now)
			.addValue("createdBy", employer.getCreatedBy());

		String sql = "INSERT INTO EMPLOYER (NAME, START_WORK_DATE, END_WORK_DATE, CREATION_DATE, CREATED_BY) "
			+ "VALUES (:name, :startWorkDate, :endWorkDate, :creationDate, :createdBy)";

		jdbcTemplate.update(sql, parameters, keyHolder);

		int id = Optional.ofNullable(keyHolder.getKey())
			.map(Number::intValue)
			.orElseThrow(() -> new RuntimeException("Failed to retrieve generated ID for Employer: " + employer));

		return employer.toBuilder().id(id).creationDate(now).build();
	}

	public List<Employer> findAll() {
		String sql = "SELECT ID, NAME, START_WORK_DATE, END_WORK_DATE, CREATED_BY, CREATION_DATE FROM EMPLOYER";

		return jdbcTemplate.query(sql, new EmployerRowMapper()).stream().toList();
	}

	public Optional<Employer> findById(int id) {
		SqlParameterSource parameters = new MapSqlParameterSource("id", id);
		String sql = "SELECT ID, NAME, START_WORK_DATE, END_WORK_DATE, CREATED_BY, CREATION_DATE FROM EMPLOYER WHERE ID = :id";

		return jdbcTemplate.query(sql, parameters, new EmployerRowMapper()).stream().findFirst();
	}

	protected static class EmployerRowMapper implements RowMapper<Employer> {
		@Override
		public Employer mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
			return Employer.builder()
				.id(rs.getInt("ID"))
				.name(rs.getString("NAME"))
				.startWorkDate(Optional.ofNullable(rs.getDate("START_WORK_DATE"))
					.map(Date::toLocalDate)
					.orElse(null))
				.endWorkDate(Optional.ofNullable(rs.getDate("END_WORK_DATE"))
					.map(Date::toLocalDate)
					.orElse(null))
				.createdBy(rs.getString("CREATED_BY"))
				.creationDate(Optional.ofNullable(rs.getTimestamp("CREATION_DATE"))
					.map(Timestamp::toLocalDateTime)
					.orElse(null))
				.build();
		}
	}
}
