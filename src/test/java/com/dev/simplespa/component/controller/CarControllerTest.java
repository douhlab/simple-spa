package com.dev.simplespa.component.controller;

import static java.time.Month.JANUARY;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.dev.simplespa.component.controller.dto.CarCreationRequest;
import com.dev.simplespa.component.controller.dto.CarExpenseCreationRequest;
import com.dev.simplespa.component.controller.dto.UserPrincipal;
import com.dev.simplespa.model.domain.car.Car;
import com.dev.simplespa.model.domain.car.CarExpense;
import com.dev.simplespa.model.domain.car.CarExpenseType;
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
	void addExpense_shouldReturnCreatedExpense() {
		var request = CarExpenseCreationRequest.builder()
			.carId(1)
			.type(CarExpenseType.FUEL)
			.numberOfDays(0)
			.description("Fuel")
			.amountInUsd(50)
			.serviceStationId(2)
			.mainOdometer(51000)
			.dateHappened(LocalDate.of(2024, JANUARY, 1))
			.note("full tank")
			.build();
		var expected = CarExpense.builder().id(10).description("Fuel").build();

		when(carService.addExpense(42, request)).thenReturn(expected);

		var principal = new UserPrincipal(42, "user1");
		CarExpense result = carController.addExpense(principal, request);

		Assertions.assertThat(result).isEqualTo(expected);
		verify(carService).addExpense(42, request);
	}

	@Test
	void findAllExpenses_shouldReturnExpensesForCurrentUser() {
		when(currentUserService.getCurrentUserId()).thenReturn(42);
		List<CarExpense> expected = List.of(CarExpense.builder().id(10).description("Fuel").build());
		when(carService.findAllExpenses(42)).thenReturn(expected);

		Assertions.assertThat(carController.findAllExpenses()).isEqualTo(expected);
		verify(carService).findAllExpenses(42);
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
