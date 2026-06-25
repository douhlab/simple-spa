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

import com.dev.simplespa.model.domain.car.CarServiceStation;
import com.dev.simplespa.model.domain.car.ServiceStationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

class CarServiceStationDaoTest {

	private NamedParameterJdbcTemplate jdbcTemplate;
	private CarServiceStationDao carServiceStationDao;

	@BeforeEach
	void setUp() {
		jdbcTemplate = mock(NamedParameterJdbcTemplate.class);
		carServiceStationDao = new CarServiceStationDao(jdbcTemplate);
	}

	@Test
	void create_shouldInsertAndReturnServiceStationWithGeneratedId() {
		CarServiceStation input = CarServiceStation.builder()
			.name("Shell Station")
			.location("Beirut")
			.type(ServiceStationType.GAS_STATION)
			.note("24/7")
			.build();

		ArgumentCaptor<KeyHolder> keyHolderCaptor = ArgumentCaptor.forClass(KeyHolder.class);

		when(jdbcTemplate.update(anyString(), any(SqlParameterSource.class), keyHolderCaptor.capture()))
			.thenAnswer(invocation -> {
				GeneratedKeyHolder kh = (GeneratedKeyHolder) keyHolderCaptor.getValue();
				kh.getKeyList().add(Map.of("GENERATED_KEY", 5));
				return 1;
			});

		CarServiceStation result = carServiceStationDao.create(input);

		assertThat(result.getId()).isEqualTo(5);
		assertThat(result.getName()).isEqualTo("Shell Station");
		assertThat(result.getType()).isEqualTo(ServiceStationType.GAS_STATION);

		verify(jdbcTemplate).update(
			contains("INSERT INTO CAR_SERVICE_STATION"),
			any(SqlParameterSource.class),
			any(KeyHolder.class)
		);
	}

	@Test
	void create_shouldThrowWhenNoGeneratedKeyReturned() {
		CarServiceStation input = CarServiceStation.builder()
			.name("Shell Station")
			.location("Beirut")
			.type(ServiceStationType.GAS_STATION)
			.build();

		when(jdbcTemplate.update(anyString(), any(), any())).thenReturn(1);

		assertThatThrownBy(() -> carServiceStationDao.create(input))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Failed to retrieve generated ID");
	}

	@Test
	void findAllShouldReturnServiceStations() {
		CarServiceStation expected = CarServiceStation.builder().id(1).name("Station").build();
		when(jdbcTemplate.query(anyString(), any(CarServiceStationDao.CarServiceStationRowMapper.class)))
			.thenReturn(List.of(expected));

		assertThat(carServiceStationDao.findAll()).containsExactly(expected);
	}

	@Test
	void findByIdShouldReturnOptionalWhenFound() {
		CarServiceStation expected = CarServiceStation.builder().id(1).name("Station").build();
		when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(CarServiceStationDao.CarServiceStationRowMapper.class)))
			.thenReturn(Collections.singletonList(expected));

		Optional<CarServiceStation> result = carServiceStationDao.findById(1);

		assertThat(result).isPresent().contains(expected);
	}

	@Test
	void findByIdShouldReturnEmptyWhenNotFound() {
		when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(CarServiceStationDao.CarServiceStationRowMapper.class)))
			.thenReturn(Collections.emptyList());

		assertThat(carServiceStationDao.findById(1)).isEmpty();
	}

	@Test
	void carServiceStationRowMapperShouldMapResultSet() throws Exception {
		ResultSet rs = mock(ResultSet.class);
		when(rs.getInt("ID")).thenReturn(42);
		when(rs.getString("NAME")).thenReturn("Shell Station");
		when(rs.getString("LOCATION")).thenReturn("Beirut");
		when(rs.getInt("TYPE")).thenReturn(ServiceStationType.GAS_STATION.getCode());
		when(rs.getString("NOTE")).thenReturn("24/7");

		CarServiceStation result = new CarServiceStationDao.CarServiceStationRowMapper().mapRow(rs, 0);

		assertThat(result.getId()).isEqualTo(42);
		assertThat(result.getName()).isEqualTo("Shell Station");
		assertThat(result.getLocation()).isEqualTo("Beirut");
		assertThat(result.getType()).isEqualTo(ServiceStationType.GAS_STATION);
		assertThat(result.getNote()).isEqualTo("24/7");
	}
}
