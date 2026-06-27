package com.dev.simplespa.component.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.dev.simplespa.model.domain.car.CarServiceStation;
import com.dev.simplespa.model.domain.car.ServiceStationType;
import com.dev.simplespa.module.car.CarServiceStationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CarServiceStationController.class)
@AutoConfigureMockMvc(addFilters = false)
class CarServiceStationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CarServiceStationService carServiceStationService;

	@Test
	void findAll_shouldReturnServiceStations() throws Exception {
		var stations = List.of(
			CarServiceStation.builder()
				.id(1)
				.name("Shell")
				.location("Beirut")
				.type(ServiceStationType.GAS_STATION)
				.note("24/7")
				.build()
		);
		when(carServiceStationService.findAll()).thenReturn(stations);

		mockMvc.perform(get("/simple-spa/v1/api/car/service-stations/all"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(1))
			.andExpect(jsonPath("$[0].name").value("Shell"))
			.andExpect(jsonPath("$[0].location").value("Beirut"))
			.andExpect(jsonPath("$[0].type").value("GAS_STATION"));

		verify(carServiceStationService).findAll();
	}
}
