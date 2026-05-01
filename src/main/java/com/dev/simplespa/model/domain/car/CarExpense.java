package com.dev.simplespa.model.domain.car;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class CarExpense {
    private int id;
    private Car car;
    private CarExpenseType type;
    private String description;
    private double amountInUsd;
    private CarServiceStation serviceStation;
    private double mainOdometer;
    private LocalDate dateHappened;
    private String note;
    private LocalDateTime creationDate;
    private String createdBy;

    private Optional<CarFuelEvent> fuelEvent;
}
