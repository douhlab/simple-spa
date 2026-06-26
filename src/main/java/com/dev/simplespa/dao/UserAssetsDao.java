package com.dev.simplespa.dao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import com.dev.simplespa.model.domain.UserAssets;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class UserAssetsDao {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public UserAssetsDao(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public UserAssets create(int userId, UserAssets userAssets) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		var now = LocalDateTime.now();

		SqlParameterSource parameters = new MapSqlParameterSource()
			.addValue("userId", userId)
			.addValue("eurAmount", userAssets.getEurAmount())
			.addValue("chfAmount", userAssets.getChfAmount())
			.addValue("audAmount", userAssets.getAudAmount())
			.addValue("cadAmount", userAssets.getCadAmount())
			.addValue("goldOunce", userAssets.getGoldOunce())
			.addValue("silverOunce", userAssets.getSilverOunce())
			.addValue("usdAmount", userAssets.getUsdAmount())
			.addValue("usdFnbAmount", userAssets.getUsdFnbAmount())
			.addValue("cryptoAmountInBtc", userAssets.getCryptoAmountInBtc())
			.addValue("gunAmountInUsd", userAssets.getGunAmountInUsd())
			.addValue("creationDate", now)
			.addValue("createdBy", userAssets.getCreatedBy());

		String sql = "INSERT INTO USER_ASSETS (USER_ID, EUR_AMOUNT, CHF_AMOUNT, AUD_AMOUNT, CAD_AMOUNT, "
			+ "GOLD_OUNCE, SILVER_OUNCE, USD_AMOUNT, USD_FNB_AMOUNT, CRYPTO_BTC_AMOUNT, GUN_AMOUNT_USD, "
			+ "CREATION_DATE, CREATED_BY) "
			+ "VALUES (:userId, :eurAmount, :chfAmount, :audAmount, :cadAmount, :goldOunce, :silverOunce, "
			+ ":usdAmount, :usdFnbAmount, :cryptoAmountInBtc, :gunAmountInUsd, :creationDate, :createdBy)";

		jdbcTemplate.update(sql, parameters, keyHolder);

		int id = Optional.ofNullable(keyHolder.getKey())
			.map(Number::intValue)
			.orElseThrow(() -> new RuntimeException("Failed to retrieve generated ID for UserAssets"));

		return userAssets.toBuilder().id(id).userId(userId).creationDate(now).build();
	}

	public UserAssets update(int userId, UserAssets userAssets) {
		SqlParameterSource parameters = new MapSqlParameterSource()
			.addValue("userId", userId)
			.addValue("eurAmount", userAssets.getEurAmount())
			.addValue("chfAmount", userAssets.getChfAmount())
			.addValue("audAmount", userAssets.getAudAmount())
			.addValue("cadAmount", userAssets.getCadAmount())
			.addValue("goldOunce", userAssets.getGoldOunce())
			.addValue("silverOunce", userAssets.getSilverOunce())
			.addValue("usdAmount", userAssets.getUsdAmount())
			.addValue("usdFnbAmount", userAssets.getUsdFnbAmount())
			.addValue("cryptoAmountInBtc", userAssets.getCryptoAmountInBtc())
			.addValue("gunAmountInUsd", userAssets.getGunAmountInUsd());

		String sql = "UPDATE USER_ASSETS SET EUR_AMOUNT = :eurAmount, CHF_AMOUNT = :chfAmount, "
			+ "AUD_AMOUNT = :audAmount, CAD_AMOUNT = :cadAmount, GOLD_OUNCE = :goldOunce, "
			+ "SILVER_OUNCE = :silverOunce, USD_AMOUNT = :usdAmount, USD_FNB_AMOUNT = :usdFnbAmount, "
			+ "CRYPTO_BTC_AMOUNT = :cryptoAmountInBtc, GUN_AMOUNT_USD = :gunAmountInUsd "
			+ "WHERE USER_ID = :userId";

		jdbcTemplate.update(sql, parameters);

		return findByUserId(userId)
			.orElseThrow(() -> new RuntimeException("UserAssets not found after update for userId: " + userId));
	}

	public Optional<UserAssets> findByUserId(int userId) {
		SqlParameterSource parameters = new MapSqlParameterSource("userId", userId);
		String sql = "SELECT ID, USER_ID, EUR_AMOUNT, CHF_AMOUNT, AUD_AMOUNT, CAD_AMOUNT, GOLD_OUNCE, "
			+ "SILVER_OUNCE, USD_AMOUNT, USD_FNB_AMOUNT, CRYPTO_BTC_AMOUNT, GUN_AMOUNT_USD, "
			+ "CREATION_DATE, CREATED_BY FROM USER_ASSETS WHERE USER_ID = :userId";

		return jdbcTemplate.query(sql, parameters, new UserAssetsRowMapper()).stream().findFirst();
	}

	protected static class UserAssetsRowMapper implements RowMapper<UserAssets> {
		@Override
		public UserAssets mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
			return UserAssets.builder()
				.id(rs.getInt("ID"))
				.userId(rs.getInt("USER_ID"))
				.eurAmount(rs.getDouble("EUR_AMOUNT"))
				.chfAmount(rs.getDouble("CHF_AMOUNT"))
				.audAmount(rs.getDouble("AUD_AMOUNT"))
				.cadAmount(rs.getDouble("CAD_AMOUNT"))
				.goldOunce(rs.getDouble("GOLD_OUNCE"))
				.silverOunce(rs.getDouble("SILVER_OUNCE"))
				.usdAmount(rs.getDouble("USD_AMOUNT"))
				.usdFnbAmount(rs.getDouble("USD_FNB_AMOUNT"))
				.cryptoAmountInBtc(rs.getDouble("CRYPTO_BTC_AMOUNT"))
				.gunAmountInUsd(rs.getDouble("GUN_AMOUNT_USD"))
				.creationDate(Optional.ofNullable(rs.getTimestamp("CREATION_DATE"))
					.map(Timestamp::toLocalDateTime)
					.orElse(null))
				.createdBy(rs.getString("CREATED_BY"))
				.build();
		}
	}
}
