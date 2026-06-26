package com.dev.simplespa.component.controller.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAssetsDto {
	private Map<String, Double> amountsForeignCy;
	private Map<String, Double> commoditiesAmountsInOunce;
	private double usdAmount;
	private double usdFnbAmount;
	private double cryptoAmountInBtc;
	private double gunAmountInUsd;
}
