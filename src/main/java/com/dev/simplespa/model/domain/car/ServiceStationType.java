package com.dev.simplespa.model.domain.car;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServiceStationType {
	GAS_STATION(1),
	INSURANCE_PROVIDER(2),
	MECHANIC_TAX(3),
	MECHANIC_CHECK_PUBLIC_SERVICE(4),
	CAR_WASH(5),
	MECHANIC_GARAGE(6),
	ELECTRIC_GARAGE(7),
	TIRE_GARAGE(8),
	BATTERY_SERVICE(9),
	EXHAUST_SERVICE(10),
	AIR_CONDITIONING_SERVICE(11),
	OIL_CHANGE(12),
	BODY_FIX(13),
	PAINT_SERVICE(14),
	OTHER(20);

	private final int code;

	public static ServiceStationType fromCode(int code) {
		return Arrays.stream(ServiceStationType.values())
			.filter(type -> type.getCode() == code)
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Invalid ServiceStationType code: " + code));
	}
}
