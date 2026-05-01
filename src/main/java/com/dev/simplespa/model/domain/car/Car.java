package com.dev.simplespa.model.domain.car;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.dev.simplespa.model.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class Car {
    private int id;

    private User user;
    private String name;
    private String model;
    private int modelYear;
    private LocalDate ownedDate;
    private double odometerWhenOwned;
    private double priceInUsdWhenOwned;
    private String note;
    private LocalDateTime creationDate;
    private String createdBy;

}
