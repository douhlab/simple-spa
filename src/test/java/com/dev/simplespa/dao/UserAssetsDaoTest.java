package com.dev.simplespa.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.dev.simplespa.model.domain.UserAssets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

class UserAssetsDaoTest {

	private NamedParameterJdbcTemplate jdbcTemplate;
	private UserAssetsDao userAssetsDao;

	@BeforeEach
	void setUp() {
		jdbcTemplate = mock(NamedParameterJdbcTemplate.class);
		userAssetsDao = new UserAssetsDao(jdbcTemplate);
	}

	@Test
	void create_shouldInsertAndReturnUserAssetsWithGeneratedId() {
		UserAssets input = sampleAssets().build();

		ArgumentCaptor<KeyHolder> keyHolderCaptor = ArgumentCaptor.forClass(KeyHolder.class);

		when(jdbcTemplate.update(anyString(), any(SqlParameterSource.class), keyHolderCaptor.capture()))
			.thenAnswer(invocation -> {
				GeneratedKeyHolder kh = (GeneratedKeyHolder) keyHolderCaptor.getValue();
				kh.getKeyList().add(Map.of("GENERATED_KEY", 42));
				return 1;
			});

		UserAssets result = userAssetsDao.create(1, input);

		assertThat(result.getId()).isEqualTo(42);
		assertThat(result.getUserId()).isEqualTo(1);
		assertThat(result.getEurAmount()).isEqualTo(536);
		assertThat(result.getCreationDate()).isNotNull();

		verify(jdbcTemplate).update(
			contains("INSERT INTO USER_ASSETS"),
			any(SqlParameterSource.class),
			any(KeyHolder.class)
		);
	}

	@Test
	void create_shouldThrowWhenNoGeneratedKeyReturned() {
		when(jdbcTemplate.update(anyString(), any(), any())).thenReturn(1);

		assertThatThrownBy(() -> userAssetsDao.create(1, sampleAssets().build()))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Failed to retrieve generated ID");
	}

	@Test
	void findByUserIdShouldReturnOptionalWhenFound() {
		UserAssets expected = sampleAssets().id(1).userId(42).build();

		when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(UserAssetsDao.UserAssetsRowMapper.class)))
			.thenReturn(Collections.singletonList(expected));

		Optional<UserAssets> result = userAssetsDao.findByUserId(42);

		assertThat(result).isPresent();
		assertThat(result.get()).isEqualTo(expected);

		ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
		verify(jdbcTemplate).query(anyString(), captor.capture(), any(UserAssetsDao.UserAssetsRowMapper.class));
		assertThat(captor.getValue().getValue("userId")).isEqualTo(42);
	}

	@Test
	void findByUserIdShouldReturnEmptyWhenNotFound() {
		when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(UserAssetsDao.UserAssetsRowMapper.class)))
			.thenReturn(Collections.emptyList());

		assertThat(userAssetsDao.findByUserId(42)).isEmpty();
	}

	@Test
	void update_shouldUpdateExistingUserAssets() {
		UserAssets existing = sampleAssets().id(1).userId(42).creationDate(LocalDateTime.now()).createdBy("system").build();
		UserAssets updatedInput = sampleAssets().usdAmount(2000).build();

		when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(UserAssetsDao.UserAssetsRowMapper.class)))
			.thenReturn(Collections.singletonList(existing.toBuilder().usdAmount(2000).build()));

		UserAssets result = userAssetsDao.update(42, updatedInput);

		assertThat(result.getUsdAmount()).isEqualTo(2000);
		verify(jdbcTemplate).update(contains("UPDATE USER_ASSETS"), any(SqlParameterSource.class));
	}

	@Test
	void userAssetsRowMapperShouldMapResultSet() throws Exception {
		ResultSet rs = mock(ResultSet.class);
		when(rs.getInt("ID")).thenReturn(1);
		when(rs.getInt("USER_ID")).thenReturn(42);
		when(rs.getDouble("EUR_AMOUNT")).thenReturn(536.0);
		when(rs.getDouble("CHF_AMOUNT")).thenReturn(556.0);
		when(rs.getDouble("AUD_AMOUNT")).thenReturn(1100.0);
		when(rs.getDouble("CAD_AMOUNT")).thenReturn(2600.0);
		when(rs.getDouble("GOLD_OUNCE")).thenReturn(20.5);
		when(rs.getDouble("SILVER_OUNCE")).thenReturn(645.0);
		when(rs.getDouble("USD_AMOUNT")).thenReturn(1000.0);
		when(rs.getDouble("USD_FNB_AMOUNT")).thenReturn(1100.0);
		when(rs.getDouble("CRYPTO_BTC_AMOUNT")).thenReturn(0.5);
		when(rs.getDouble("GUN_AMOUNT_USD")).thenReturn(1500.0);
		when(rs.getString("CREATED_BY")).thenReturn("system");
		when(rs.getTimestamp("CREATION_DATE"))
			.thenReturn(Timestamp.valueOf(LocalDateTime.of(2026, 6, 27, 12, 0)));

		UserAssets result = new UserAssetsDao.UserAssetsRowMapper().mapRow(rs, 0);

		assertThat(result.getUserId()).isEqualTo(42);
		assertThat(result.getEurAmount()).isEqualTo(536);
		assertThat(result.getCryptoAmountInBtc()).isEqualTo(0.5);
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
