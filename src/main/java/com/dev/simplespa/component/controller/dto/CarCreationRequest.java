package com.dev.simplespa.component.controller.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarCreationRequest {
    private String name;
    private String model;
    private int modelYear;
    private LocalDate ownedDate;
    private double odometerWhenOwned;
    private double priceInUsdWhenOwned;
    private String note;
}
