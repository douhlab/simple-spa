package com.dev.simplespa.model.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyFirst {
    private int id;
    private String description;
    private String createdBy;
    private LocalDateTime creationDate;

}
