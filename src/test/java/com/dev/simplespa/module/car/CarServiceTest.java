package com.dev.simplespa.module.car;

import static java.time.Month.JANUARY;
import static java.time.Month.JUNE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.dev.simplespa.component.controller.dto.CarCreationRequest;
import com.dev.simplespa.component.controller.dto.CarExpenseCreationRequest;
import com.dev.simplespa.dao.CarDao;
import com.dev.simplespa.dao.CarExpenseDao;
import com.dev.simplespa.dao.CarFuelEventDao;
import com.dev.simplespa.dao.CarServiceStationDao;
import com.dev.simplespa.model.domain.User;
import com.dev.simplespa.model.domain.car.Car;
import com.dev.simplespa.model.domain.car.CarExpense;
import com.dev.simplespa.model.domain.car.CarExpenseType;
import com.dev.simplespa.model.domain.car.CarFuelEvent;
import com.dev.simplespa.model.domain.car.CarServiceStation;
import com.dev.simplespa.model.domain.car.FuelType;
import com.dev.simplespa.model.domain.car.ServiceStationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

	@Mock
	private CarDao carDao;
	@Mock
	private CarExpenseDao carExpenseDao;
	@Mock
	private CarFuelEventDao carFuelEventDao;
	@Mock
	private CarServiceStationDao carServiceStationDao;

	private CarService carService;

	@BeforeEach
	void setUp() {
		carService = new CarService(carDao, carExpenseDao, carFuelEventDao, carServiceStationDao);
	}

	@Test
	void add_shouldCreateCarForUser() {
		var request = CarCreationRequest.builder()
			.name("My Car")
			.model("Civic")
			.modelYear(2020)
			.ownedDate(LocalDate.of(2024, JANUARY, 1))
			.odometerWhenOwned(50000)
			.priceInUsdWhenOwned(15000)
			.note("note")
			.build();

		var created = Car.builder().id(1).name("My Car").build();
		when(carDao.create(any(Car.class))).thenReturn(created);

		Car result = carService.add(42, request);

		assertThat(result).isEqualTo(created);
		verify(carDao).create(any(Car.class));
	}

	@Test
	void findAll_shouldFilterByUserId() {
		var carForUser = Car.builder().id(1).user(User.builder().id(42).build()).name("Mine").build();
		var carForOther = Car.builder().id(2).user(User.builder().id(99).build()).name("Other").build();
		when(carDao.findAll()).thenReturn(List.of(carForUser, carForOther));

		List<Car> result = carService.findAll(42);

		assertThat(result).containsExactly(carForUser);
	}

	@Test
	void findById_shouldDelegateToDao() {
		var car = Car.builder().id(1).name("Car").build();
		when(carDao.findById(1)).thenReturn(Optional.of(car));

		assertThat(carService.findById(1)).contains(car);
	}

	@Test
	void addExpense_shouldCreateExpense() {
		var car = Car.builder().id(1).build();
		var station = CarServiceStation.builder().id(2).name("Shell").type(ServiceStationType.GAS_STATION).build();
		var request = CarExpenseCreationRequest.builder()
			.carId(1)
			.type(CarExpenseType.FUEL)
			.numberOfDays(0)
			.description("Fuel")
			.amountInUsd(50)
			.serviceStationId(2)
			.mainOdometer(51000)
			.dateHappened(LocalDate.of(2024, JUNE, 1))
			.note("full tank")
			.build();

		var createdExpense = CarExpense.builder().id(10).description("Fuel").build();

		when(carDao.findById(1)).thenReturn(Optional.of(car));
		when(carServiceStationDao.findById(2)).thenReturn(Optional.of(station));
		when(carExpenseDao.create(any(CarExpense.class))).thenReturn(createdExpense);

		CarExpense result = carService.addExpense(request);

		assertThat(result).isEqualTo(createdExpense);
		verify(carExpenseDao).create(any(CarExpense.class));
	}

	@Test
	void addExpense_shouldCreateFuelEventWhenPresent() {
		var car = Car.builder().id(1).build();
		var station = CarServiceStation.builder().id(2).build();
		var fuelEvent = CarFuelEvent.builder()
			.refillAmountInLiter(40)
			.mileageTraversed(500)
			.fuelType(FuelType.GASOLINE_95)
			.build();
		var request = CarExpenseCreationRequest.builder()
			.carId(1)
			.type(CarExpenseType.FUEL)
			.numberOfDays(0)
			.description("Fuel")
			.amountInUsd(50)
			.serviceStationId(2)
			.dateHappened(LocalDate.of(2024, JUNE, 1))
			.fuelEvent(fuelEvent)
			.build();

		var createdExpense = CarExpense.builder().id(10).description("Fuel").build();
		var createdFuelEvent = CarFuelEvent.builder().id(20).carExpenseId(10).build();

		when(carDao.findById(1)).thenReturn(Optional.of(car));
		when(carServiceStationDao.findById(2)).thenReturn(Optional.of(station));
		when(carExpenseDao.create(any(CarExpense.class))).thenReturn(createdExpense);
		when(carFuelEventDao.create(any(CarFuelEvent.class))).thenReturn(createdFuelEvent);

		carService.addExpense(request);

		verify(carFuelEventDao).create(any(CarFuelEvent.class));
	}

	@Test
	void addExpense_shouldThrowWhenCarNotFound() {
		var request = CarExpenseCreationRequest.builder()
			.carId(99)
			.type(CarExpenseType.FUEL)
			.numberOfDays(0)
			.description("Fuel")
			.amountInUsd(50)
			.serviceStationId(2)
			.dateHappened(LocalDate.of(2024, JUNE, 1))
			.build();

		when(carDao.findById(99)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> carService.addExpense(request))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Car not found");
	}

	@Test
	void addExpense_shouldThrowWhenServiceStationNotFound() {
		var car = Car.builder().id(1).build();
		var request = CarExpenseCreationRequest.builder()
			.carId(1)
			.type(CarExpenseType.FUEL)
			.numberOfDays(0)
			.description("Fuel")
			.amountInUsd(50)
			.serviceStationId(99)
			.dateHappened(LocalDate.of(2024, JUNE, 1))
			.build();

		when(carDao.findById(1)).thenReturn(Optional.of(car));
		when(carServiceStationDao.findById(99)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> carService.addExpense(request))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Service station not found");
	}

	@Test
	void findExpensesByCarId_shouldFilterByCarId() {
		var expenseForCar = CarExpense.builder().car(Car.builder().id(1).build()).description("mine").build();
		var expenseForOther = CarExpense.builder().car(Car.builder().id(2).build()).description("other").build();
		when(carExpenseDao.findAll()).thenReturn(List.of(expenseForCar, expenseForOther));

		List<CarExpense> result = carService.findExpensesByCarId(1);

		assertThat(result).containsExactly(expenseForCar);
	}

	@Test
	void findExpenseById_shouldDelegateToDao() {
		var expense = CarExpense.builder().id(1).description("expense").build();
		when(carExpenseDao.findById(1)).thenReturn(Optional.of(expense));

		assertThat(carService.findExpenseById(1)).contains(expense);
	}
}
