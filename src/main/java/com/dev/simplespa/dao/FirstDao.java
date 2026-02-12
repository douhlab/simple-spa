package com.dev.simplespa.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import com.dev.simplespa.model.domain.MyFirst;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class FirstDao {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public FirstDao(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Optional<MyFirst> findById(int id) {
		SqlParameterSource parameters = new MapSqlParameterSource("id", id);
		String sql = "SELECT ID, DESCRIPTION, CREATION_DATE, CREATED_BY FROM MY_FIRST WHERE ID = :id";

		return jdbcTemplate.query(sql, parameters, new UserRowMapper()).stream().findFirst();
	}

	protected static class UserRowMapper implements RowMapper<MyFirst> {
		@Override
		public MyFirst mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
			return MyFirst.builder()
				.id(rs.getInt("ID"))
				.description(rs.getString("DESCRIPTION"))
				.creationDate(Optional.ofNullable(rs.getTimestamp("CREATION_DATE"))
					.map(Timestamp::toLocalDateTime)
					.orElse(null))
				.createdBy(rs.getString("CREATED_BY"))
				.build();
		}
	}

	public List<MyFirst> findAll() {
		String sql = "SELECT ID, DESCRIPTION, CREATION_DATE, CREATED_BY FROM MY_FIRST";

		return jdbcTemplate.query(
			sql,
			(rs, rowNum) ->
				MyFirst.builder()
					.id(rs.getInt("ID"))
					.description(rs.getString("DESCRIPTION"))
					.creationDate(Optional.ofNullable(rs.getTimestamp("CREATION_DATE"))
						.map(Timestamp::toLocalDateTime)
						.orElse(null))
					.createdBy(rs.getString("CREATED_BY"))
					.build()
		);

	}
}
