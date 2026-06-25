package com.dev.simplespa.dao;

import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.dev.simplespa.model.domain.User;
import com.dev.simplespa.model.domain.car.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

class CarDaoTest {

	private NamedParameterJdbcTemplate jdbcTemplate;
	private CarDao carDao;

	@BeforeEach
	void setUp() {
		jdbcTemplate = mock(NamedParameterJdbcTemplate.class);
		carDao = new CarDao(jdbcTemplate);
	}

	@Test
	void create_shouldInsertAndReturnCarWithGeneratedId() {
		Car input = Car.builder()
			.user(User.builder().id(1).build())
			.name("My Car")
			.model("Civic")
			.modelYear(2020)
			.ownedDate(LocalDate.of(2024, JANUARY, 1))
			.odometerWhenOwned(50000)
			.priceInUsdWhenOwned(15000)
			.note("test note")
			.build();

		ArgumentCaptor<KeyHolder> keyHolderCaptor = ArgumentCaptor.forClass(KeyHolder.class);

		when(jdbcTemplate.update(anyString(), any(SqlParameterSource.class), keyHolderCaptor.capture()))
			.thenAnswer(invocation -> {
				GeneratedKeyHolder kh = (GeneratedKeyHolder) keyHolderCaptor.getValue();
				kh.getKeyList().add(Map.of("GENERATED_KEY", 42));
				return 1;
			});

		Car result = carDao.create(input);

		assertThat(result.getId()).isEqualTo(42);
		assertThat(result.getName()).isEqualTo("My Car");
		assertThat(result.getCreationDate()).isNotNull();

		verify(jdbcTemplate).update(
			contains("INSERT INTO CAR"),
			any(SqlParameterSource.class),
			any(KeyHolder.class)
		);
	}

	@Test
	void create_shouldThrowWhenNoGeneratedKeyReturned() {
		Car input = Car.builder()
			.user(User.builder().id(1).build())
			.name("My Car")
			.model("Civic")
			.modelYear(2020)
			.build();

		when(jdbcTemplate.update(anyString(), any(), any())).thenReturn(1);

		assertThatThrownBy(() -> carDao.create(input))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Failed to retrieve generated ID");
	}

	@Test
	void findAllShouldReturnCars() {
		Car expected = Car.builder().id(1).name("Car 1").build();
		when(jdbcTemplate.query(anyString(), any(CarDao.CarRowMapper.class)))
			.thenReturn(List.of(expected));

		List<Car> result = carDao.findAll();

		assertThat(result).containsExactly(expected);
		verify(jdbcTemplate).query(contains("FROM CAR"), any(CarDao.CarRowMapper.class));
	}

	@Test
	void findByIdShouldReturnOptionalWhenFound() {
		Car expected = Car.builder().id(1).name("Car 1").build();
		when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(CarDao.CarRowMapper.class)))
			.thenReturn(Collections.singletonList(expected));

		Optional<Car> result = carDao.findById(1);

		assertThat(result).isPresent();
		assertThat(result).contains(expected);

		ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
		verify(jdbcTemplate).query(anyString(), captor.capture(), any(CarDao.CarRowMapper.class));
		assertThat(captor.getValue().getValue("id")).isEqualTo(1);
	}

	@Test
	void findByIdShouldReturnEmptyWhenNotFound() {
		when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(CarDao.CarRowMapper.class)))
			.thenReturn(Collections.emptyList());

		assertThat(carDao.findById(1)).isEmpty();
	}

	@Test
	void carRowMapperShouldMapResultSet() throws Exception {
		ResultSet rs = mock(ResultSet.class);
		when(rs.getInt("ID")).thenReturn(42);
		when(rs.getInt("USER_ID")).thenReturn(1);
		when(rs.getString("NAME")).thenReturn("My Car");
		when(rs.getString("MODEL")).thenReturn("Civic");
		when(rs.getInt("MODEL_YEAR")).thenReturn(2020);
		when(rs.getDate("OWNED_DATE")).thenReturn(Date.valueOf(LocalDate.of(2024, JANUARY, 1)));
		when(rs.getDouble("ODOMETER_WHEN_OWNED")).thenReturn(50000.0);
		when(rs.getDouble("PRICE_IN_USD_WHEN_OWNED")).thenReturn(15000.0);
		when(rs.getString("NOTE")).thenReturn("note");
		when(rs.getString("CREATED_BY")).thenReturn("me");
		when(rs.getTimestamp("CREATION_DATE"))
			.thenReturn(Timestamp.valueOf(LocalDateTime.of(2026, FEBRUARY, 12, 12, 0)));

		Car result = new CarDao.CarRowMapper().mapRow(rs, 0);

		assertThat(result.getId()).isEqualTo(42);
		assertThat(result.getUser().getId()).isEqualTo(1);
		assertThat(result.getName()).isEqualTo("My Car");
		assertThat(result.getModel()).isEqualTo("Civic");
		assertThat(result.getModelYear()).isEqualTo(2020);
		assertThat(result.getOwnedDate()).isEqualTo(LocalDate.of(2024, JANUARY, 1));
		assertThat(result.getCreatedBy()).isEqualTo("me");
	}
}
