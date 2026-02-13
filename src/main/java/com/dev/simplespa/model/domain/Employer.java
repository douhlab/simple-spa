package com.dev.simplespa.model.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

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
public class Employer {
    private int id;
    @NotEmpty
    private String name;
    private LocalDate startWorkDate;
    private LocalDate endWorkDate;
    @NotEmpty
    @Email
    private String createdBy;
    private LocalDateTime creationDate;
}
