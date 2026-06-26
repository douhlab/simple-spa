package com.dev.simplespa.component.controller.dto;

import java.util.Map;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserAssetsUpsertRequest {
	@NotNull
	private Map<String, @PositiveOrZero Double> amountsForeignCy;

	@NotNull
	private Map<String, @PositiveOrZero Double> commoditiesAmountsInOunce;

	@PositiveOrZero
	private double usdAmount;

	@PositiveOrZero
	private double usdFnbAmount;

	@PositiveOrZero
	private double cryptoAmountInBtc;

	@PositiveOrZero
	private double gunAmountInUsd;
}
