package com.dev.simplespa.module.userassets;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.dev.simplespa.component.controller.dto.UserAssetsDto;
import com.dev.simplespa.component.controller.dto.UserAssetsUpsertRequest;
import com.dev.simplespa.dao.UserAssetsDao;
import com.dev.simplespa.model.domain.UserAssets;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAssetsService {

	private final UserAssetsDao userAssetsDao;

	public Optional<UserAssetsDto> findByUserId(int userId) {
		return userAssetsDao.findByUserId(userId).map(this::toDto);
	}

	public UserAssetsDto upsert(int userId, UserAssetsUpsertRequest request) {
		var userAssets = fromRequest(request);
		var saved = userAssetsDao.findByUserId(userId)
			.map(existing -> userAssetsDao.update(userId, userAssets.toBuilder()
				.id(existing.getId())
				.userId(userId)
				.creationDate(existing.getCreationDate())
				.createdBy(existing.getCreatedBy())
				.build()))
			.orElseGet(() -> userAssetsDao.create(userId, userAssets));

		return toDto(saved);
	}

	private UserAssets fromRequest(UserAssetsUpsertRequest request) {
		return UserAssets.builder()
			.eurAmount(requireAmount(request.getAmountsForeignCy(), "EUR"))
			.chfAmount(requireAmount(request.getAmountsForeignCy(), "CHF"))
			.audAmount(requireAmount(request.getAmountsForeignCy(), "AUD"))
			.cadAmount(requireAmount(request.getAmountsForeignCy(), "CAD"))
			.goldOunce(requireAmount(request.getCommoditiesAmountsInOunce(), "gold"))
			.silverOunce(requireAmount(request.getCommoditiesAmountsInOunce(), "silver"))
			.usdAmount(request.getUsdAmount())
			.usdFnbAmount(request.getUsdFnbAmount())
			.cryptoAmountInBtc(request.getCryptoAmountInBtc())
			.gunAmountInUsd(request.getGunAmountInUsd())
			.createdBy("system")
			.build();
	}

	private double requireAmount(Map<String, Double> values, String key) {
		return Optional.ofNullable(values.get(key))
			.orElseThrow(() -> new IllegalArgumentException("Missing required value for key: " + key));
	}

	private UserAssetsDto toDto(UserAssets userAssets) {
		var foreignCy = new LinkedHashMap<String, Double>();
		foreignCy.put("EUR", userAssets.getEurAmount());
		foreignCy.put("CHF", userAssets.getChfAmount());
		foreignCy.put("AUD", userAssets.getAudAmount());
		foreignCy.put("CAD", userAssets.getCadAmount());

		var commodities = new LinkedHashMap<String, Double>();
		commodities.put("gold", userAssets.getGoldOunce());
		commodities.put("silver", userAssets.getSilverOunce());

		return UserAssetsDto.builder()
			.amountsForeignCy(foreignCy)
			.commoditiesAmountsInOunce(commodities)
			.usdAmount(userAssets.getUsdAmount())
			.usdFnbAmount(userAssets.getUsdFnbAmount())
			.cryptoAmountInBtc(userAssets.getCryptoAmountInBtc())
			.gunAmountInUsd(userAssets.getGunAmountInUsd())
			.build();
	}
}
