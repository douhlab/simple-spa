package com.dev.simplespa.component.controller;

import java.util.List;
import java.util.Optional;

import com.dev.simplespa.component.controller.dto.CarCreationRequest;
import com.dev.simplespa.component.controller.dto.CarExpenseCreationRequest;
import com.dev.simplespa.component.controller.dto.UserPrincipal;
import com.dev.simplespa.model.domain.car.Car;
import com.dev.simplespa.model.domain.car.CarExpense;
import com.dev.simplespa.module.car.CarService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/${application.product}")
public class CarController {

	private final CurrentUserService currentUserService;
	private final CarService carService;

	@PostMapping("/v1/api/car")
	@ResponseStatus(value = HttpStatus.CREATED)
	public Car create(@AuthenticationPrincipal UserPrincipal currentUser,
		@RequestBody @Valid CarCreationRequest carCreationRequest) {

		return carService.add(currentUser.getId(), carCreationRequest);
	}

	@PostMapping("/v1/api/car/expenses")
	@ResponseStatus(value = HttpStatus.CREATED)
	public CarExpense addExpense(@AuthenticationPrincipal UserPrincipal currentUser,
		@RequestBody @Valid CarExpenseCreationRequest request) {
		return carService.addExpense(currentUser.getId(), request);
	}

	@GetMapping("/v1/api/car/expenses/all")
	public List<CarExpense> findAllExpenses() {
		return carService.findAllExpenses(currentUserService.getCurrentUserId());
	}

	@GetMapping("/v1/api/car/all")
	public List<Car> findAll() {
		int userId = currentUserService.getCurrentUserId();
		return carService.findAll(userId);
	}

	@GetMapping("/v1/api/car")
	public Optional<Car> findById(@RequestParam @Positive int id) {
		return carService.findById(id);
	}
}
