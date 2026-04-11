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

	public User register(User user) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		var now = LocalDateTime.now();
		var createdBy = "user";

		SqlParameterSource parameters = new MapSqlParameterSource()
			.addValue("username", user.getUsername())
			.addValue("email", user.getEmail())
			.addValue("passwordHash", user.getPasswordHash())
			.addValue("name", user.getName())
			.addValue("lastName", user.getLastName())
			.addValue("phoneNumber", user.getPhoneNumber())
			.addValue("creationDate", now)
			.addValue("createdBy", createdBy);

		String sql =
			"INSERT INTO USERS (USERNAME, EMAIL, PASSWORD_HASH, NAME, LAST_NAME, PHONE_NUMBER, CREATION_DATE, CREATED_BY) "
				+ "VALUES (:username, :email, :passwordHash, :name, :lastName, :phoneNumber, :creationDate, :createdBy)";

		jdbcTemplate.update(sql, parameters, keyHolder);

		int id = Optional.ofNullable(keyHolder.getKey())
			.map(Number::intValue)
			.orElseThrow(() -> new RuntimeException("Failed to retrieve generated ID for User: " + user));

		return user.toBuilder().id(id).creationDate(now).build();
	}

	public List<User> findAll() {
		String sql = "SELECT ID, USERNAME, EMAIL, PASSWORD_HASH, NAME, LAST_NAME, PHONE_NUMBER, CREATION_DATE, "
			+ "CREATED_BY FROM USERS";

		return jdbcTemplate.query(sql, new UserRowMapper());
	}

	public Optional<User> findById(int id) {
		SqlParameterSource parameters = new MapSqlParameterSource("id", id);
		String sql = "SELECT ID, USERNAME, EMAIL, PASSWORD_HASH, NAME, LAST_NAME, PHONE_NUMBER, CREATION_DATE, "
			+ "CREATED_BY FROM USERS WHERE ID = :id";

		return jdbcTemplate.query(sql, parameters, new UserRowMapper()).stream().findFirst();
	}

	protected static class UserRowMapper implements RowMapper<User> {
		@Override
		public User mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
			return User.builder()
				.id(rs.getInt("ID"))
				.username(rs.getString("USERNAME"))
				.email(rs.getString("EMAIL"))
				.passwordHash(rs.getString("PASSWORD_HASH"))
				.name(rs.getString("NAME"))
				.lastName(rs.getString("LAST_NAME"))
				.phoneNumber(rs.getString("PHONE_NUMBER"))
				.creationDate(Optional.ofNullable(rs.getTimestamp("CREATION_DATE"))
					.map(Timestamp::toLocalDateTime)
					.orElse(null))
				.createdBy(rs.getString("CREATED_BY"))
				.build();
		}
	}
}
