package com.dev.simplespa.component.controller.dto;

import java.time.LocalDate;

import com.dev.simplespa.model.domain.car.CarExpenseType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarExpenseCreationRequest {
    @Positive
    private int carId;

    @NotNull
    private CarExpenseType type;

    @NotNull
    private double numberOfDays;
    @NotEmpty
    private String description;
    @Positive
    private double amountInUsd;

    @NotNull
    private int serviceStationId;
    private double mainOdometer;
    @NotNull
    private LocalDate dateHappened;
    private String note;

    private CarFuelEventCreationRequest fuelEvent;
}
