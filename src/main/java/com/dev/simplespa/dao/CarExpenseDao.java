package com.dev.simplespa.dao;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.dev.simplespa.model.domain.car.Car;
import com.dev.simplespa.model.domain.car.CarExpense;
import com.dev.simplespa.model.domain.car.CarExpenseType;
import com.dev.simplespa.model.domain.car.CarServiceStation;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class CarExpenseDao {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public CarExpenseDao(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public CarExpense create(CarExpense carExpense) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		var now = LocalDateTime.now();

		SqlParameterSource parameters = new MapSqlParameterSource()
			.addValue("carId", carExpense.getCar().getId())
			.addValue("typeCode", carExpense.getType().getCode())
			.addValue("description", carExpense.getDescription())
			.addValue("amountInUsd", carExpense.getAmountInUsd())
			.addValue("serviceStationId", carExpense.getServiceStation().getId())
			.addValue("mainOdometer", carExpense.getMainOdometer())
			.addValue("dateHappened", carExpense.getDateHappened())
			.addValue("note", carExpense.getNote())
			.addValue("creationDate", now)
			.addValue("createdBy", carExpense.getCreatedBy());

		String sql = "INSERT INTO CAR_EXPENSE (CAR_ID, TYPE_CODE, DESCRIPTION, AMOUNT_IN_USD, SERVICE_STATION_ID,"
			+ "MAIN_ODOMETER, DATE_HAPPENED, NOTE, CREATION_DATE, CREATED_BY) "
			+ "VALUES (:carId, :typeCode, :description, :amountInUsd, :serviceStationId,"
			+ ":mainOdometer, :dateHappened, :note, :creationDate, :createdBy)";

		jdbcTemplate.update(sql, parameters, keyHolder);

		int id = Optional.ofNullable(keyHolder.getKey())
			.map(Number::intValue)
			.orElseThrow(() -> new RuntimeException("Failed to retrieve generated ID for carExpense: " + carExpense));

		return carExpense.toBuilder().id(id).creationDate(now).build();
	}

	public List<CarExpense> findAll() {
		String sql = "SELECT ID, CAR_ID, TYPE_CODE, DESCRIPTION, AMOUNT_IN_USD, SERVICE_STATION_ID, MAIN_ODOMETER, "
			+ "DATE_HAPPENED, NOTE, CREATION_DATE, CREATED_BY FROM CAR_EXPENSE";

		return jdbcTemplate.query(sql, new CarExpenseRowMapper()).stream().toList();
	}

	public Optional<CarExpense> findById(int id) {
		SqlParameterSource parameters = new MapSqlParameterSource("id", id);
		String sql = "SELECT ID, CAR_ID, TYPE_CODE, DESCRIPTION, AMOUNT_IN_USD, SERVICE_STATION_ID, MAIN_ODOMETER, "
			+ "DATE_HAPPENED, NOTE, CREATION_DATE, CREATED_BY FROM CAR_EXPENSE WHERE ID = :id";

		return jdbcTemplate.query(sql, parameters, new CarExpenseRowMapper()).stream().findFirst();
	}

	protected static class CarExpenseRowMapper implements RowMapper<CarExpense> {
		@Override
		public CarExpense mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
			return CarExpense.builder()
				.id(rs.getInt("ID"))
				.car(Car.builder().id(rs.getInt("CAR_ID")).build())
				.type(CarExpenseType.fromCode(rs.getInt("TYPE_CODE")))
				.description(rs.getString("DESCRIPTION"))
				.amountInUsd(rs.getDouble("AMOUNT_IN_USD"))
				.serviceStation(CarServiceStation.builder().id(rs.getInt("SERVICE_STATION_ID")).build())
				.mainOdometer(rs.getDouble("MAIN_ODOMETER"))
				.dateHappened(Optional.ofNullable(rs.getDate("DATE_HAPPENED"))
					.map(Date::toLocalDate)
					.orElse(null))
				.note(rs.getString("NOTE"))
				.creationDate(Optional.ofNullable(rs.getTimestamp("CREATION_DATE"))
					.map(Timestamp::toLocalDateTime)
					.orElse(null))
				.createdBy(rs.getString("CREATED_BY"))
				.build();
		}
	}
}
