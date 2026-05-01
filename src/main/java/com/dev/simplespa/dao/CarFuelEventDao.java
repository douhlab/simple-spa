package com.dev.simplespa.dao;

import java.util.List;
import java.util.Optional;

import com.dev.simplespa.model.domain.car.CarFuelEvent;
import com.dev.simplespa.model.domain.car.FuelType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class CarFuelEventDao {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public CarFuelEventDao(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public CarFuelEvent create(CarFuelEvent carFuelEvent) {
		KeyHolder keyHolder = new GeneratedKeyHolder();

		SqlParameterSource parameters = new MapSqlParameterSource()
			.addValue("carExpenseId", carFuelEvent.getCarExpenseId())
			.addValue("refillAmountInLiter", carFuelEvent.getRefillAmountInLiter())
			.addValue("mileageTraversed", carFuelEvent.getMileageTraversed())
			.addValue("fuelTypeCode", carFuelEvent.getFuelType().getCode());

		String sql =
			"INSERT INTO CAR_FUEL_EVENT (CAR_EXPENSE_ID, REFILL_AMOUNT_IN_LITER, MILEAGE_TRAVERSED, FUEL_TYPE_CODE) "
				+ "VALUES (:carExpenseId, :refillAmountInLiter, :mileageTraversed, :fuelTypeCode)";

		jdbcTemplate.update(sql, parameters, keyHolder);

		int id = Optional.ofNullable(keyHolder.getKey())
			.map(Number::intValue)
			.orElseThrow(() -> new RuntimeException(
				"Failed to retrieve generated ID for carFuelEvent: " + carFuelEvent));

		return carFuelEvent.toBuilder().id(id).build();
	}

	public List<CarFuelEvent> findAll() {
		String sql = "SELECT CAR_EXPENSE_ID, REFILL_AMOUNT_IN_LITER, MILEAGE_TRAVERSED, FUEL_TYPE_CODE "
			+ "FROM CAR_FUEL_EVENT";

		return jdbcTemplate.query(sql, new CarFuelEventRowMapper()).stream().toList();
	}

	public Optional<CarFuelEvent> findById(int id) {
		SqlParameterSource parameters = new MapSqlParameterSource("id", id);
		String sql = "SELECT CAR_EXPENSE_ID, REFILL_AMOUNT_IN_LITER, MILEAGE_TRAVERSED, FUEL_TYPE_CODE "
			+ "FROM CAR_FUEL_EVENT WHERE ID = :id";

		return jdbcTemplate.query(sql, parameters, new CarFuelEventRowMapper()).stream().findFirst();
	}

	protected static class CarFuelEventRowMapper implements RowMapper<CarFuelEvent> {
		@Override
		public CarFuelEvent mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
			return CarFuelEvent.builder()
				.id(rs.getInt("ID"))
				.carExpenseId(rs.getInt("CAR_EXPENSE_ID"))
				.refillAmountInLiter(rs.getDouble("REFILL_AMOUNT_IN_LITER"))
				.mileageTraversed(rs.getDouble("MILEAGE_TRAVERSED"))
				.fuelType(FuelType.fromCode(rs.getInt("FUEL_TYPE_CODE")))
				.build();
		}
	}
}
