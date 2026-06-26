package com.dev.simplespa.component.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import com.dev.simplespa.component.controller.dto.UserAssetsDto;
import com.dev.simplespa.component.controller.dto.UserAssetsUpsertRequest;
import com.dev.simplespa.module.userassets.UserAssetsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserAssetsControllerTest {

	@Mock
	private UserAssetsService userAssetsService;

	@Mock
	private CurrentUserService currentUserService;

	@InjectMocks
	private UserAssetsController userAssetsController;

	@Test
	void findForCurrentUser_shouldReturnUserAssets() {
		var dto = UserAssetsDto.builder()
			.amountsForeignCy(Map.of(
				"EUR", 536.0,
				"CHF", 556.0,
				"AUD", 1100.0,
				"CAD", 2600.0
			))
			.commoditiesAmountsInOunce(Map.of(
				"gold", 20.5,
				"silver", 645.0
			))
			.usdAmount(1000)
			.usdFnbAmount(1100)
			.cryptoAmountInBtc(0.5)
			.gunAmountInUsd(1500)
			.build();

		when(currentUserService.getCurrentUserId()).thenReturn(42);
		when(userAssetsService.findByUserId(42)).thenReturn(Optional.of(dto));

		Optional<UserAssetsDto> result = userAssetsController.findForCurrentUser();

		assertThat(result).contains(dto);
		verify(userAssetsService).findByUserId(42);
	}

	@Test
	void upsert_shouldSaveUserAssetsForCurrentUser() {
		var request = UserAssetsUpsertRequest.builder()
			.amountsForeignCy(Map.of("EUR", 536.0, "CHF", 556.0, "AUD", 1100.0, "CAD", 2600.0))
			.commoditiesAmountsInOunce(Map.of("gold", 20.5, "silver", 645.0))
			.usdAmount(1000)
			.usdFnbAmount(1100)
			.cryptoAmountInBtc(0.5)
			.gunAmountInUsd(1500)
			.build();
		var dto = UserAssetsDto.builder()
			.amountsForeignCy(request.getAmountsForeignCy())
			.commoditiesAmountsInOunce(request.getCommoditiesAmountsInOunce())
			.usdAmount(1000)
			.usdFnbAmount(1100)
			.cryptoAmountInBtc(0.5)
			.gunAmountInUsd(1500)
			.build();

		when(currentUserService.getCurrentUserId()).thenReturn(42);
		when(userAssetsService.upsert(42, request)).thenReturn(dto);

		UserAssetsDto result = userAssetsController.upsert(request);

		assertThat(result).isEqualTo(dto);
		verify(userAssetsService).upsert(42, request);
	}
}
