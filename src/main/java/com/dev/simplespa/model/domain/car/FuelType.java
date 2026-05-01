package com.dev.simplespa.model.domain.car;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FuelType {
    GASOLINE_95(1),
    GASOLINE_98(2),
    DIESEL(3),
    ELECTRIC(4),
    HYBRID(5);

    private final int code;

    public static FuelType fromCode(int code) {
        return Arrays.stream(FuelType.values())
                .filter(type -> type.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid FuelType code: " + code));
    }
}
