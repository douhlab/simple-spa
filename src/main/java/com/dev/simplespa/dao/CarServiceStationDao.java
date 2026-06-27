package com.dev.simplespa.dao;

import java.util.List;
import java.util.Optional;

import com.dev.simplespa.model.domain.car.CarServiceStation;
import com.dev.simplespa.model.domain.car.ServiceStationType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class CarServiceStationDao {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public CarServiceStationDao(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public CarServiceStation create(CarServiceStation carServiceStation) {
		KeyHolder keyHolder = new GeneratedKeyHolder();

		SqlParameterSource parameters = new MapSqlParameterSource()
			.addValue("name", carServiceStation.getName())
			.addValue("location", carServiceStation.getLocation())
			.addValue("typeCode", carServiceStation.getType().getCode())
			.addValue("note", carServiceStation.getNote());

		String sql =
			"INSERT INTO CAR_SERVICE_STATION (NAME, LOCATION, TYPE_CODE, NOTE) VALUES (:name, :location, :typeCode, :note)";

		jdbcTemplate.update(sql, parameters, keyHolder);

		int id = Optional.ofNullable(keyHolder.getKey())
			.map(Number::intValue)
			.orElseThrow(() -> new RuntimeException(
				"Failed to retrieve generated ID for CarServiceStation: " + carServiceStation));

		return carServiceStation.toBuilder().id(id).build();
	}

	public List<CarServiceStation> findAll() {
		String sql = "SELECT ID, NAME, LOCATION, TYPE_CODE, NOTE FROM CAR_SERVICE_STATION";

		return jdbcTemplate.query(sql, new CarServiceStationRowMapper()).stream().toList();
	}

	public Optional<CarServiceStation> findById(int id) {
		SqlParameterSource parameters = new MapSqlParameterSource("id", id);
		String sql = "SELECT ID, NAME, LOCATION, TYPE_CODE, NOTE FROM CAR_SERVICE_STATION WHERE ID = :id";

		return jdbcTemplate.query(sql, parameters, new CarServiceStationRowMapper()).stream().findFirst();
	}

	protected static class CarServiceStationRowMapper implements RowMapper<CarServiceStation> {
		@Override
		public CarServiceStation mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
			return CarServiceStation.builder()
				.id(rs.getInt("ID"))
				.name(rs.getString("NAME"))
				.location(rs.getString("LOCATION"))
				.type(ServiceStationType.fromCode(rs.getInt("TYPE_CODE")))
				.note(rs.getString("NOTE"))
				.build();
		}
	}
}
