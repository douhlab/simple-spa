package com.dev.simplespa.component.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@SpringBootTest(classes = CorsConfig.class)
class CorsConfigTest {

	private final CorsConfig corsConfig = new CorsConfig();

	@Test
	void corsConfigurationSource_shouldContainExpectedCorsConfiguration() {

		CorsConfigurationSource source =
			corsConfig.corsConfigurationSource();

		assertInstanceOf(
			UrlBasedCorsConfigurationSource.class,
			source
		);

		CorsConfiguration configuration =
			source.getCorsConfiguration(
				new org.springframework.mock.web.MockHttpServletRequest()
			);

		assertNotNull(configuration);

		assertEquals(
			List.of(
				"http://localhost:4200",
				"https://brave-sky-0fd134503.6.azurestaticapps.net",
				"https://www.heyhayhuy.dev",
				"https://heyhayhuy.dev"
			),
			configuration.getAllowedOrigins()
		);

		assertEquals(
			List.of(
				"GET",
				"POST",
				"PUT",
				"DELETE",
				"OPTIONS"
			),
			configuration.getAllowedMethods()
		);

		assertEquals(
			List.of("*"),
			configuration.getAllowedHeaders()
		);

		assertTrue(configuration.getAllowCredentials());
	}

	@Test
	void shouldAllowLocalhost4200Origin() {

		CorsConfigurationSource source =
			corsConfig.corsConfigurationSource();

		MockHttpServletRequest request =
			new MockHttpServletRequest();

		request.setRequestURI("/simple-spa/login");

		CorsConfiguration configuration =
			source.getCorsConfiguration(request);

		assertNotNull(configuration);

		assertTrue(configuration.checkOrigin("http://localhost:4200") != null);
	}

	@Test
	void shouldRejectUnknownOrigin() {

		CorsConfigurationSource source =
			corsConfig.corsConfigurationSource();

		MockHttpServletRequest request =
			new MockHttpServletRequest();

		request.setRequestURI("/simple-spa/login");

		CorsConfiguration configuration =
			source.getCorsConfiguration(request);

		assertNull(
			configuration.checkOrigin("https://evil-site.com")
		);
	}
}
