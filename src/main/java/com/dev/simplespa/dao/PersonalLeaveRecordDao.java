package com.dev.simplespa.dao;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.dev.simplespa.model.domain.Employer;
import com.dev.simplespa.model.domain.PersonalLeaveRecord;
import com.dev.simplespa.model.domain.PersonalLeaveRecordCreationRequest;
import com.dev.simplespa.model.domain.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class PersonalLeaveRecordDao {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public PersonalLeaveRecordDao(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public PersonalLeaveRecord create(PersonalLeaveRecordCreationRequest request) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		var now = LocalDateTime.now();
		var createdBy = "createdByMe";

		SqlParameterSource parameters = new MapSqlParameterSource()
			.addValue("userId", request.getUserId())
			.addValue("employerId", request.getEmployerId())
			.addValue("dateHappened", request.getDateHappened())
			.addValue("noOfDays", request.getNumberOfDays())
			.addValue("description", request.getDescription())
			.addValue("currentBalance", request.getCurrentBalance())
			.addValue("note", request.getNote())
			.addValue("creationDate", now)
			.addValue("createdBy", createdBy);

		String sql =
			"INSERT INTO PERSONAL_LEAVE_RECORD (USER_ID, EMPLOYER_ID, DATE_HAPPENED, NO_OF_DAYS, DESCRIPTION, CURRENT_BALANCE,NOTE,CREATION_DATE,CREATED_BY) "
				+ "VALUES (:userId, :employerId, :dateHappened, :noOfDays, :description, :currentBalance, :note, :creationDate, :createdBy)";

		jdbcTemplate.update(sql, parameters, keyHolder);

		int id = Optional.ofNullable(keyHolder.getKey())
			.map(Number::intValue)
			.orElseThrow(() -> new RuntimeException(
				"Failed to retrieve generated ID for PersonalLeaveRecord: " + request));

		return PersonalLeaveRecord.fromCreationRequest(request)
			.toBuilder()
			.id(id)
			.creationDate(now)
			.createdBy(createdBy)
			.build();
	}

	public List<PersonalLeaveRecord> findAll(int userId) {
		SqlParameterSource parameters = new MapSqlParameterSource("userId", userId);

		String sql =
			"SELECT ID, USER_ID, EMPLOYER_ID, DATE_HAPPENED, NO_OF_DAYS, DESCRIPTION, CURRENT_BALANCE,"
				+ " NOTE, CREATION_DATE, CREATED_BY FROM PERSONAL_LEAVE_RECORD"
				+ " WHERE USER_ID = :userId";

		return jdbcTemplate.query(sql, parameters, new PersonalLeaveRecordRowMapper()).stream().toList();
	}

	public Optional<PersonalLeaveRecord> findById(int id) {
		SqlParameterSource parameters = new MapSqlParameterSource("id", id);
		String sql =
			"SELECT ID, USER_ID, EMPLOYER_ID, DATE_HAPPENED, NO_OF_DAYS, DESCRIPTION, CURRENT_BALANCE,"
				+ "NOTE,CREATION_DATE,CREATED_BY FROM PERSONAL_LEAVE_RECORD WHERE ID = :id";

		return jdbcTemplate.query(sql, parameters, new PersonalLeaveRecordRowMapper()).stream().findFirst();
	}

	public void deleteById(int id) {
		String sql = "delete from PERSONAL_LEAVE_RECORD where ID = :id";
		SqlParameterSource parameters = new MapSqlParameterSource("id", id);
		jdbcTemplate.update(sql, parameters);
	}

	protected static class PersonalLeaveRecordRowMapper implements RowMapper<PersonalLeaveRecord> {
		@Override
		public PersonalLeaveRecord mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
			return PersonalLeaveRecord.builder()
				.id(rs.getInt("ID"))
				.user(User.builder().id(rs.getInt("USER_ID")).build())
				.employer(Employer.builder().id(rs.getInt("EMPLOYER_ID")).build())
				.dateHappened(Optional.ofNullable(rs.getDate("DATE_HAPPENED"))
					.map(Date::toLocalDate)
					.orElse(null))
				.numberOfDays(rs.getDouble("NO_OF_DAYS"))
				.description(rs.getString("DESCRIPTION"))
				.currentBalance(rs.getDouble("CURRENT_BALANCE"))
				.note(rs.getString("NOTE"))
				.createdBy(rs.getString("CREATED_BY"))
				.creationDate(Optional.ofNullable(rs.getTimestamp("CREATION_DATE"))
					.map(Timestamp::toLocalDateTime)
					.orElse(null))
				.build();
		}
	}
}
