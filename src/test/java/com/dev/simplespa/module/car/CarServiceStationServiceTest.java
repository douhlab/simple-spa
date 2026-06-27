package com.dev.simplespa.module.car;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.dev.simplespa.dao.CarServiceStationDao;
import com.dev.simplespa.model.domain.car.CarServiceStation;
import com.dev.simplespa.model.domain.car.ServiceStationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarServiceStationServiceTest {

	@Mock
	private CarServiceStationDao carServiceStationDao;

	@InjectMocks
	private CarServiceStationService carServiceStationService;

	@Test
	void findAll_shouldReturnAllServiceStations() {
		var stations = List.of(
			CarServiceStation.builder().id(1).name("Shell").type(ServiceStationType.GAS_STATION).build(),
			CarServiceStation.builder().id(2).name("Garage").type(ServiceStationType.MECHANIC_GARAGE).build()
		);
		when(carServiceStationDao.findAll()).thenReturn(stations);

		assertThat(carServiceStationService.findAll()).isEqualTo(stations);
		verify(carServiceStationDao).findAll();
	}
}
