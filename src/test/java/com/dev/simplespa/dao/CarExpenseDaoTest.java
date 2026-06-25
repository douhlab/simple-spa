package com.dev.simplespa.dao;

import static java.time.Month.FEBRUARY;
import static java.time.Month.JUNE;
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

import com.dev.simplespa.model.domain.car.Car;
import com.dev.simplespa.model.domain.car.CarExpense;
import com.dev.simplespa.model.domain.car.CarExpenseType;
import com.dev.simplespa.model.domain.car.CarServiceStation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

class CarExpenseDaoTest {

	private NamedParameterJdbcTemplate jdbcTemplate;
	private CarExpenseDao carExpenseDao;

	@BeforeEach
	void setUp() {
		jdbcTemplate = mock(NamedParameterJdbcTemplate.class);
		carExpenseDao = new CarExpenseDao(jdbcTemplate);
	}

	@Test
	void create_shouldInsertAndReturnExpenseWithGeneratedId() {
		CarExpense input = CarExpense.builder()
			.car(Car.builder().id(1).build())
			.type(CarExpenseType.FUEL)
			.description("Fuel refill")
			.amountInUsd(50)
			.serviceStation(CarServiceStation.builder().id(2).build())
			.mainOdometer(51000)
			.dateHappened(LocalDate.of(2024, JUNE, 1))
			.note("full tank")
			.build();

		ArgumentCaptor<KeyHolder> keyHolderCaptor = ArgumentCaptor.forClass(KeyHolder.class);

		when(jdbcTemplate.update(anyString(), any(SqlParameterSource.class), keyHolderCaptor.capture()))
			.thenAnswer(invocation -> {
				GeneratedKeyHolder kh = (GeneratedKeyHolder) keyHolderCaptor.getValue();
				kh.getKeyList().add(Map.of("GENERATED_KEY", 99));
				return 1;
			});

		CarExpense result = carExpenseDao.create(input);

		assertThat(result.getId()).isEqualTo(99);
		assertThat(result.getDescription()).isEqualTo("Fuel refill");
		assertThat(result.getCreationDate()).isNotNull();

		verify(jdbcTemplate).update(
			contains("INSERT INTO CAR_EXPENSE"),
			any(SqlParameterSource.class),
			any(KeyHolder.class)
		);
	}

	@Test
	void create_shouldThrowWhenNoGeneratedKeyReturned() {
		CarExpense input = CarExpense.builder()
			.car(Car.builder().id(1).build())
			.type(CarExpenseType.FUEL)
			.description("Fuel refill")
			.amountInUsd(50)
			.serviceStation(CarServiceStation.builder().id(2).build())
			.dateHappened(LocalDate.of(2024, JUNE, 1))
			.build();

		when(jdbcTemplate.update(anyString(), any(), any())).thenReturn(1);

		assertThatThrownBy(() -> carExpenseDao.create(input))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Failed to retrieve generated ID");
	}

	@Test
	void findAllShouldReturnExpenses() {
		CarExpense expected = CarExpense.builder().id(1).description("expense").build();
		when(jdbcTemplate.query(anyString(), any(CarExpenseDao.CarExpenseRowMapper.class)))
			.thenReturn(List.of(expected));

		assertThat(carExpenseDao.findAll()).containsExactly(expected);
	}

	@Test
	void findByIdShouldReturnOptionalWhenFound() {
		CarExpense expected = CarExpense.builder().id(1).description("expense").build();
		when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(CarExpenseDao.CarExpenseRowMapper.class)))
			.thenReturn(Collections.singletonList(expected));

		Optional<CarExpense> result = carExpenseDao.findById(1);

		assertThat(result).isPresent();
		assertThat(result).contains(expected);
	}

	@Test
	void findByIdShouldReturnEmptyWhenNotFound() {
		when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(CarExpenseDao.CarExpenseRowMapper.class)))
			.thenReturn(Collections.emptyList());

		assertThat(carExpenseDao.findById(1)).isEmpty();
	}

	@Test
	void carExpenseRowMapperShouldMapResultSet() throws Exception {
		ResultSet rs = mock(ResultSet.class);
		when(rs.getInt("ID")).thenReturn(42);
		when(rs.getInt("CAR_ID")).thenReturn(1);
		when(rs.getInt("TYPE_CODE")).thenReturn(CarExpenseType.FUEL.getCode());
		when(rs.getString("DESCRIPTION")).thenReturn("Fuel refill");
		when(rs.getDouble("AMOUNT_IN_USD")).thenReturn(50.0);
		when(rs.getInt("SERVICE_STATION_ID")).thenReturn(2);
		when(rs.getDouble("MAIN_ODOMETER")).thenReturn(51000.0);
		when(rs.getDate("DATE_HAPPENED")).thenReturn(Date.valueOf(LocalDate.of(2024, JUNE, 1)));
		when(rs.getString("NOTE")).thenReturn("note");
		when(rs.getString("CREATED_BY")).thenReturn("me");
		when(rs.getTimestamp("CREATION_DATE"))
			.thenReturn(Timestamp.valueOf(LocalDateTime.of(2026, FEBRUARY, 12, 12, 0)));

		CarExpense result = new CarExpenseDao.CarExpenseRowMapper().mapRow(rs, 0);

		assertThat(result.getId()).isEqualTo(42);
		assertThat(result.getCar().getId()).isEqualTo(1);
		assertThat(result.getType()).isEqualTo(CarExpenseType.FUEL);
		assertThat(result.getDescription()).isEqualTo("Fuel refill");
		assertThat(result.getServiceStation().getId()).isEqualTo(2);
	}
}
