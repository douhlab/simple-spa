package com.dev.simplespa.module.userassets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import com.dev.simplespa.component.controller.dto.UserAssetsDto;
import com.dev.simplespa.component.controller.dto.UserAssetsUpsertRequest;
import com.dev.simplespa.dao.UserAssetsDao;
import com.dev.simplespa.model.domain.UserAssets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserAssetsServiceTest {

	@Mock
	private UserAssetsDao userAssetsDao;

	@InjectMocks
	private UserAssetsService userAssetsService;

	@Test
	void findByUserId_shouldReturnDtoWhenFound() {
		when(userAssetsDao.findByUserId(42)).thenReturn(Optional.of(sampleAssets().id(1).userId(42).build()));

		Optional<UserAssetsDto> result = userAssetsService.findByUserId(42);

		assertThat(result).isPresent();
		assertThat(result.get().getAmountsForeignCy()).containsEntry("EUR", 536.0);
		assertThat(result.get().getCommoditiesAmountsInOunce()).containsEntry("gold", 20.5);
		assertThat(result.get().getUsdAmount()).isEqualTo(1000);
		assertThat(result.get().getCryptoAmountInBtc()).isEqualTo(0.5);
	}

	@Test
	void findByUserId_shouldReturnEmptyWhenNotFound() {
		when(userAssetsDao.findByUserId(42)).thenReturn(Optional.empty());

		assertThat(userAssetsService.findByUserId(42)).isEmpty();
	}

	@Test
	void upsert_shouldCreateWhenUserAssetsDoNotExist() {
		var request = sampleRequest();
		var created = sampleAssets().id(1).userId(42).creationDate(LocalDateTime.now()).build();

		when(userAssetsDao.findByUserId(42)).thenReturn(Optional.empty());
		when(userAssetsDao.create(eq(42), any(UserAssets.class))).thenReturn(created);

		UserAssetsDto result = userAssetsService.upsert(42, request);

		assertThat(result.getGunAmountInUsd()).isEqualTo(1500);
		verify(userAssetsDao).create(eq(42), any(UserAssets.class));
	}

	@Test
	void upsert_shouldUpdateWhenUserAssetsExist() {
		var request = sampleRequest().toBuilder().usdAmount(2000).build();
		var existing = sampleAssets().id(1).userId(42).creationDate(LocalDateTime.now()).createdBy("system").build();
		var updated = existing.toBuilder().usdAmount(2000).build();

		when(userAssetsDao.findByUserId(42)).thenReturn(Optional.of(existing));
		when(userAssetsDao.update(eq(42), any(UserAssets.class))).thenReturn(updated);

		UserAssetsDto result = userAssetsService.upsert(42, request);

		assertThat(result.getUsdAmount()).isEqualTo(2000);
		verify(userAssetsDao).update(eq(42), any(UserAssets.class));
	}

	@Test
	void upsert_shouldFailWhenRequiredForeignCurrencyMissing() {
		var request = UserAssetsUpsertRequest.builder()
			.amountsForeignCy(Map.of("EUR", 536.0))
			.commoditiesAmountsInOunce(Map.of("gold", 20.5, "silver", 645.0))
			.usdAmount(1000)
			.usdFnbAmount(1100)
			.cryptoAmountInBtc(0.5)
			.gunAmountInUsd(1500)
			.build();

		assertThatThrownBy(() -> userAssetsService.upsert(42, request))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("CHF");
	}

	private UserAssetsUpsertRequest sampleRequest() {
		return UserAssetsUpsertRequest.builder()
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
	}

	private UserAssets.UserAssetsBuilder sampleAssets() {
		return UserAssets.builder()
			.eurAmount(536)
			.chfAmount(556)
			.audAmount(1100)
			.cadAmount(2600)
			.goldOunce(20.5)
			.silverOunce(645)
			.usdAmount(1000)
			.usdFnbAmount(1100)
			.cryptoAmountInBtc(0.5)
			.gunAmountInUsd(1500)
			.createdBy("system");
	}
}
