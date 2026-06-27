package com.dev.simplespa.component.controller.dto;

import com.dev.simplespa.model.domain.car.FuelType;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarFuelEventCreationRequest {
	@PositiveOrZero
	private double refillAmountInLiter;

	@PositiveOrZero
	private double mileageTraversed;

	private FuelType fuelType;
}
