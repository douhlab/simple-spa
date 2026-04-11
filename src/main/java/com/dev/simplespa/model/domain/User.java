package com.dev.simplespa.model.domain;

import java.time.LocalDateTime;

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
public class User {
    private int id;

    private String username;
    private String email;
    private String passwordHash;

    private String name;
    private String lastName;
    private String phoneNumber;

    private String createdBy;
    private LocalDateTime creationDate;

}
