package com.dev.simplespa.component.controller.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class PersonalLeaveRecordCreationRequest {
    @Positive
    private int userId;
    @Positive
    private int employerId;
    @NotNull
    private LocalDate dateHappened;
    @NotNull
    private double numberOfDays;
    @NotEmpty
    private String description;
    private Double currentBalance;
    private String note;
}
