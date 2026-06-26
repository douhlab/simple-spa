package com.dev.simplespa.dao;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.dev.simplespa.model.domain.User;
import com.dev.simplespa.model.domain.car.Car;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class CarDao {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public CarDao(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Car create(Car car) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		var now = LocalDateTime.now();

		SqlParameterSource parameters = new MapSqlParameterSource()
			.addValue("userId", car.getUser().getId())
			.addValue("name", car.getName())
			.addValue("model", car.getModel())
			.addValue("modelYear", car.getModelYear())
			.addValue("ownedDate", car.getOwnedDate())
			.addValue("odometerWhenOwned", car.getOdometerWhenOwned())
			.addValue("priceInUsdWhenOwned", car.getPriceInUsdWhenOwned())
			.addValue("note", car.getNote())
			.addValue("creationDate", now)
			.addValue("createdBy", car.getCreatedBy());

		String sql =
			"INSERT INTO CAR (USER_ID, NAME, MODEL, MODEL_YEAR, OWNED_DATE, ODOMETER_WHEN_OWNED,"
				+ "PRICE_IN_USD_WHEN_OWNED, NOTE, CREATION_DATE, CREATED_BY) VALUES (:userId, :name, :model, "
				+ ":modelYear, :ownedDate, :odometerWhenOwned, :priceInUsdWhenOwned, :note, :creationDate, :createdBy)";

		jdbcTemplate.update(sql, parameters, keyHolder);

		int id = Optional.ofNullable(keyHolder.getKey())
			.map(Number::intValue)
			.orElseThrow(() -> new RuntimeException("Failed to retrieve generated ID for car: " + car));

		return car.toBuilder().id(id).creationDate(now).build();
	}

	public List<Car> findAll() {
		String sql = "SELECT ID, USER_ID, NAME, MODEL, MODEL_YEAR, OWNED_DATE, ODOMETER_WHEN_OWNED,"
			+ "PRICE_IN_USD_WHEN_OWNED, NOTE, CREATION_DATE, CREATED_BY FROM CAR";

		return jdbcTemplate.query(sql, new CarRowMapper()).stream().toList();
	}

	public Optional<Car> findById(int id) {
		SqlParameterSource parameters = new MapSqlParameterSource("id", id);

		String sql = "SELECT ID, USER_ID, NAME, MODEL, MODEL_YEAR, OWNED_DATE, ODOMETER_WHEN_OWNED,"
			+ "PRICE_IN_USD_WHEN_OWNED, NOTE, CREATION_DATE, CREATED_BY FROM CAR WHERE ID = :id";

		return jdbcTemplate.query(sql, parameters, new CarRowMapper()).stream().findFirst();
	}

	protected static class CarRowMapper implements RowMapper<Car> {
		@Override
		public Car mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
			return Car.builder()
				.id(rs.getInt("ID"))
				.user(User.builder().id(rs.getInt("USER_ID")).build())
				.name(rs.getString("NAME"))
				.model(rs.getString("MODEL"))
				.modelYear(rs.getInt("MODEL_YEAR"))
				.ownedDate(Optional.ofNullable(rs.getDate("OWNED_DATE"))
					.map(Date::toLocalDate)
					.orElse(null))
				.odometerWhenOwned(rs.getDouble("ODOMETER_WHEN_OWNED"))
				.priceInUsdWhenOwned(rs.getDouble("PRICE_IN_USD_WHEN_OWNED"))
				.note(rs.getString("NOTE"))
				.creationDate(Optional.ofNullable(rs.getTimestamp("CREATION_DATE"))
					.map(Timestamp::toLocalDateTime)
					.orElse(null))
				.createdBy(rs.getString("CREATED_BY"))
				.build();
		}
	}
}
