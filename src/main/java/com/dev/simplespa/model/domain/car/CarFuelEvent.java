package com.dev.simplespa.model.domain.car;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class CarFuelEvent {
    private int id;
    private int carExpenseId;
    private double refillAmountInLiter;
    private double mileageTraversed;
    private FuelType fuelType;

}
