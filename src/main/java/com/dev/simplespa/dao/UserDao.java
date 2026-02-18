package com.dev.simplespa.dao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.dev.simplespa.model.domain.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public UserDao(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public User create(User user) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		var now = LocalDateTime.now();

		SqlParameterSource parameters = new MapSqlParameterSource()
			.addValue("name", user.getName())
			.addValue("creationDate", now)
			.addValue("createdBy", user.getCreatedBy());

		String sql = "INSERT INTO USERS (NAME, CREATION_DATE, CREATED_BY) VALUES (:name, :creationDate, :createdBy)";

		jdbcTemplate.update(sql, parameters, keyHolder);

		int id = Optional.ofNullable(keyHolder.getKey())
			.map(Number::intValue)
			.orElseThrow(() -> new RuntimeException("Failed to retrieve generated ID for User: " + user));

		return user.toBuilder().id(id).creationDate(now).build();
	}

	public Optional<User> findById(int id) {
		SqlParameterSource parameters = new MapSqlParameterSource("id", id);
		String sql = "SELECT ID, NAME, CREATION_DATE, CREATED_BY FROM USERS WHERE ID = :id";

		return jdbcTemplate.query(sql, parameters, new UserRowMapper()).stream().findFirst();
	}

	protected static class UserRowMapper implements RowMapper<User> {
		@Override
		public User mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
			return User.builder()
				.id(rs.getInt("ID"))
				.name(rs.getString("NAME"))
				.creationDate(Optional.ofNullable(rs.getTimestamp("CREATION_DATE"))
					.map(Timestamp::toLocalDateTime)
					.orElse(null))
				.createdBy(rs.getString("CREATED_BY"))
				.build();
		}
	}

	public List<User> findAll() {
		String sql = "SELECT ID, NAME, CREATION_DATE, CREATED_BY FROM USERS";

		return jdbcTemplate.query(
			sql,
			(rs, rowNum) ->
				User.builder()
					.id(rs.getInt("ID"))
					.name(rs.getString("NAME"))
					.creationDate(Optional.ofNullable(rs.getTimestamp("CREATION_DATE"))
						.map(Timestamp::toLocalDateTime)
						.orElse(null))
					.createdBy(rs.getString("CREATED_BY"))
					.build()
		);

	}
}
