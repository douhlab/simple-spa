package com.dev.simplespa.model.domain.car;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CarExpenseType {
    FUEL(1),
    MAINTENANCE(2),
    REPAIR(3),
    INSURANCE(4),
    TAX(5),
    OTHER(10);

    private final int code;

    public static CarExpenseType fromCode(int code) {
        return Arrays.stream(CarExpenseType.values())
                .filter(type -> type.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid CarExpenseType code: " + code));
    }
}
