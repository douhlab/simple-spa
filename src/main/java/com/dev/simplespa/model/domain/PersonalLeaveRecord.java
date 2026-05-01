package com.dev.simplespa.model.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.dev.simplespa.component.controller.dto.PersonalLeaveRecordCreationRequest;
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
public class PersonalLeaveRecord {
    private int id;
    private User user;
    private Employer employer;
    private LocalDate dateHappened;
    private double numberOfDays;
    private String description;
    private double currentBalance;
    private String note;
    private LocalDateTime creationDate;
    private String createdBy;

    public static PersonalLeaveRecord fromCreationRequest(PersonalLeaveRecordCreationRequest request) {
        return PersonalLeaveRecord.builder()
            .user(User.builder().id(request.getUserId()).build())
            .employer(Employer.builder().id(request.getEmployerId()).build())
            .dateHappened(request.getDateHappened())
            .numberOfDays(request.getNumberOfDays())
            .description(request.getDescription())
            .currentBalance(request.getCurrentBalance())
            .note(request.getNote())
            .build();
    }
}
