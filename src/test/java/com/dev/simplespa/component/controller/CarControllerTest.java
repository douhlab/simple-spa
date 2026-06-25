package com.dev.simplespa.component.controller;

import static java.time.Month.JANUARY;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.dev.simplespa.component.controller.dto.CarCreationRequest;
import com.dev.simplespa.component.controller.dto.UserPrincipal;
import com.dev.simplespa.model.domain.car.Car;
import com.dev.simplespa.module.car.CarService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarControllerTest {

	@Mock
	private CarService carService;

	@Mock
	private CurrentUserService currentUserService;

	@InjectMocks
	private CarController carController;

	@Test
	void create_shouldReturnCreatedCar() {
		var request = CarCreationRequest.builder()
			.name("My Car")
			.model("Civic")
			.modelYear(2020)
			.ownedDate(LocalDate.of(2024, JANUARY, 1))
			.odometerWhenOwned(50000)
			.priceInUsdWhenOwned(15000)
			.note("test")
			.build();

		var expected = Car.builder().id(1).name("My Car").model("Civic").modelYear(2020).build();
		when(carService.add(42, request)).thenReturn(expected);

		var principal = new UserPrincipal(42, "user1");
		Car result = carController.create(principal, request);

		Assertions.assertThat(result).isEqualTo(expected);
		verify(carService).add(42, request);
	}

	@Test
	void findAll_shouldReturnCarsForCurrentUser() {
		when(currentUserService.getCurrentUserId()).thenReturn(42);
		List<Car> expected = List.of(Car.builder().id(1).name("My Car").build());
		when(carService.findAll(42)).thenReturn(expected);

		Assertions.assertThat(carController.findAll()).isEqualTo(expected);
	}

	@Test
	void findById_shouldReturnCar() {
		var expected = Car.builder().id(1).name("My Car").build();
		when(carService.findById(1)).thenReturn(Optional.of(expected));

		Assertions.assertThat(carController.findById(1)).contains(expected);
	}
}
