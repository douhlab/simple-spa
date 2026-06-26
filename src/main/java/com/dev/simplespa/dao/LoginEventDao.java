package com.dev.simplespa.dao;

import java.time.LocalDateTime;
import java.util.Optional;

import com.dev.simplespa.model.domain.LoginEvent;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class LoginEventDao {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public LoginEventDao(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public LoginEvent create(LoginEvent loginEvent) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		var now = Optional.ofNullable(loginEvent.getEventDate()).orElseGet(LocalDateTime::now);

		SqlParameterSource parameters = new MapSqlParameterSource()
			.addValue("userId", loginEvent.getUserId())
			.addValue("username", loginEvent.getUsername())
			.addValue("status", loginEvent.getStatus().name())
			.addValue("failureReason", loginEvent.getFailureReason())
			.addValue("ipAddress", loginEvent.getIpAddress())
			.addValue("userAgent", loginEvent.getUserAgent())
			.addValue("eventDate", now)
			.addValue("createdBy", loginEvent.getCreatedBy());

		String sql = "INSERT INTO LOGIN_EVENT (USER_ID, USERNAME, STATUS, FAILURE_REASON, IP_ADDRESS, "
			+ "USER_AGENT, EVENT_DATE, CREATED_BY) "
			+ "VALUES (:userId, :username, :status, :failureReason, :ipAddress, :userAgent, :eventDate, :createdBy)";

		jdbcTemplate.update(sql, parameters, keyHolder);

		int id = Optional.ofNullable(keyHolder.getKey())
			.map(Number::intValue)
			.orElseThrow(() -> new RuntimeException("Failed to retrieve generated ID for LoginEvent: " + loginEvent));

		return loginEvent.toBuilder().id(id).eventDate(now).build();
	}
}
