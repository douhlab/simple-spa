package com.dev.simplespa.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.dev.simplespa.model.domain.car.CarFuelEvent;
import com.dev.simplespa.model.domain.car.FuelType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

class CarFuelEventDaoTest {

	private NamedParameterJdbcTemplate jdbcTemplate;
	private CarFuelEventDao carFuelEventDao;

	@BeforeEach
	void setUp() {
		jdbcTemplate = mock(NamedParameterJdbcTemplate.class);
		carFuelEventDao = new CarFuelEventDao(jdbcTemplate);
	}

	@Test
	void create_shouldInsertAndReturnFuelEventWithGeneratedId() {
		CarFuelEvent input = CarFuelEvent.builder()
			.carExpenseId(10)
			.refillAmountInLiter(40)
			.mileageTraversed(500)
			.fuelType(FuelType.GASOLINE_95)
			.build();

		ArgumentCaptor<KeyHolder> keyHolderCaptor = ArgumentCaptor.forClass(KeyHolder.class);

		when(jdbcTemplate.update(anyString(), any(SqlParameterSource.class), keyHolderCaptor.capture()))
			.thenAnswer(invocation -> {
				GeneratedKeyHolder kh = (GeneratedKeyHolder) keyHolderCaptor.getValue();
				kh.getKeyList().add(Map.of("GENERATED_KEY", 7));
				return 1;
			});

		CarFuelEvent result = carFuelEventDao.create(input);

		assertThat(result.getId()).isEqualTo(7);
		assertThat(result.getCarExpenseId()).isEqualTo(10);
		assertThat(result.getFuelType()).isEqualTo(FuelType.GASOLINE_95);

		verify(jdbcTemplate).update(
			contains("INSERT INTO CAR_FUEL_EVENT"),
			any(SqlParameterSource.class),
			any(KeyHolder.class)
		);
	}

	@Test
	void create_shouldThrowWhenNoGeneratedKeyReturned() {
		CarFuelEvent input = CarFuelEvent.builder()
			.carExpenseId(10)
			.refillAmountInLiter(40)
			.mileageTraversed(500)
			.fuelType(FuelType.GASOLINE_95)
			.build();

		when(jdbcTemplate.update(anyString(), any(), any())).thenReturn(1);

		assertThatThrownBy(() -> carFuelEventDao.create(input))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Failed to retrieve generated ID");
	}

	@Test
	void findAllShouldReturnFuelEvents() {
		CarFuelEvent expected = CarFuelEvent.builder().id(1).carExpenseId(10).build();
		when(jdbcTemplate.query(anyString(), any(CarFuelEventDao.CarFuelEventRowMapper.class)))
			.thenReturn(List.of(expected));

		assertThat(carFuelEventDao.findAll()).containsExactly(expected);
	}

	@Test
	void findByIdShouldReturnOptionalWhenFound() {
		CarFuelEvent expected = CarFuelEvent.builder().id(1).carExpenseId(10).build();
		when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(CarFuelEventDao.CarFuelEventRowMapper.class)))
			.thenReturn(Collections.singletonList(expected));

		Optional<CarFuelEvent> result = carFuelEventDao.findById(1);

		assertThat(result).isPresent();
		assertThat(result.get()).isEqualTo(expected);
	}

	@Test
	void findByIdShouldReturnEmptyWhenNotFound() {
		when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(CarFuelEventDao.CarFuelEventRowMapper.class)))
			.thenReturn(Collections.emptyList());

		assertThat(carFuelEventDao.findById(1)).isEmpty();
	}

	@Test
	void carFuelEventRowMapperShouldMapResultSet() throws Exception {
		ResultSet rs = mock(ResultSet.class);
		when(rs.getInt("ID")).thenReturn(42);
		when(rs.getInt("CAR_EXPENSE_ID")).thenReturn(10);
		when(rs.getDouble("REFILL_AMOUNT_IN_LITER")).thenReturn(40.0);
		when(rs.getDouble("MILEAGE_TRAVERSED")).thenReturn(500.0);
		when(rs.getInt("FUEL_TYPE_CODE")).thenReturn(FuelType.DIESEL.getCode());

		CarFuelEvent result = new CarFuelEventDao.CarFuelEventRowMapper().mapRow(rs, 0);

		assertThat(result.getId()).isEqualTo(42);
		assertThat(result.getCarExpenseId()).isEqualTo(10);
		assertThat(result.getRefillAmountInLiter()).isEqualTo(40);
		assertThat(result.getFuelType()).isEqualTo(FuelType.DIESEL);
	}
}
