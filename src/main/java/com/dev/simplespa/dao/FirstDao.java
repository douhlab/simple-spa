package com.dev.simplespa.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import com.dev.simplespa.model.domain.MyFirst;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FirstDao {

	private final JdbcTemplate jdbcTemplate;

	public FirstDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Optional<MyFirst> findById(int id) {
		String sql = "SELECT ID, DESCRIPTION, CREATION_DATE, CREATED_BY FROM MY_FIRST WHERE ID = ?";

		var x = jdbcTemplate.queryForObject(
			sql,
			new Object[]{id},
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

		return Optional.ofNullable(x);
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
