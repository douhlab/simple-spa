package com.dev.simplespa.module.car;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarService {

	private final CarDao carDao;
	private final CarExpenseDao carExpenseDao;
	private final CarFuelEventDao carFuelEventDao;
	private final CarServiceStationDao carServiceStationDao;

	public Car add(int userId, CarCreationRequest request) {
		var car = Car.builder().user(User.builder().id(userId).build()).name(request.getName())
			.model(request.getModel())
			.modelYear(request.getModelYear())
			.ownedDate(request.getOwnedDate())
			.odometerWhenOwned(request.getOdometerWhenOwned())
			.priceInUsdWhenOwned(request.getPriceInUsdWhenOwned())
			.note(request.getNote())
			.build();
		return carDao.create(car);
	}

	public List<Car> findAll(int userId) {
		return carDao.findAll().stream().filter(car -> car.getUser().getId() == userId).toList();
	}

	public Optional<Car> findById(int id) {
		return carDao.findById(id);
	}

	public CarExpense addExpense(int userId, CarExpenseCreationRequest request) {
        var car = carDao.findById(request.getCarId())
            .orElseThrow(() -> new RuntimeException("Car not found with ID: " + request.getCarId()));

		if (car.getUser().getId() != userId) {
			throw new RuntimeException("Car not found with ID: " + request.getCarId());
		}

		var serviceStation = carServiceStationDao.findById(request.getServiceStationId())
			.orElseThrow(() -> new RuntimeException(
				"Service station not found with ID: " + request.getServiceStationId()));

		var expenseToCreate = CarExpense.builder()
			.car(car)
            .type(request.getType())
            .description(request.getDescription())
            .amountInUsd(request.getAmountInUsd())
            .serviceStation(serviceStation)
            .mainOdometer(request.getMainOdometer())
            .dateHappened(request.getDateHappened())
            .note(request.getNote())
			.build();

		var expense =  carExpenseDao.create(expenseToCreate);

		return Optional.ofNullable(request.getFuelEvent()).map(event -> {
			var fuelEventToCreate = event.toBuilder().carExpenseId(expense.getId()).build();
			var fuelEvent = carFuelEventDao.create(fuelEventToCreate);
			return expense.toBuilder().fuelEvent(Optional.of(fuelEvent)).build();
		}).orElse(expense);
	}

	public List<CarExpense> findExpensesByCarId(int carId) {
		return carExpenseDao.findAll().stream().filter(expense -> expense.getCar().getId() == carId).toList();
	}

	public List<CarExpense> findAllExpenses(int userId) {
		var userCarIds = findAll(userId).stream().map(Car::getId).toList();
		return carExpenseDao.findAll().stream()
			.filter(expense -> userCarIds.contains(expense.getCar().getId()))
			.toList();
	}

	public Optional<CarExpense> findExpenseById(int id) {
		return carExpenseDao.findById(id);
	}
}
