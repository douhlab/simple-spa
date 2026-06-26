package com.dev.simplespa.model.domain;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class UserAssets {
	private int id;
	private int userId;
	private double eurAmount;
	private double chfAmount;
	private double audAmount;
	private double cadAmount;
	private double goldOunce;
	private double silverOunce;
	private double usdAmount;
	private double usdFnbAmount;
	private double cryptoAmountInBtc;
	private double gunAmountInUsd;
	private LocalDateTime creationDate;
	private String createdBy;
}
