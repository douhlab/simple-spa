package com.dev.simplespa.component.controller;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.Optional;

import com.dev.simplespa.component.controller.util.JwtUtil;
import com.dev.simplespa.model.domain.RegistrationRequest;
import com.dev.simplespa.model.domain.User;
import com.dev.simplespa.module.RecaptchaService;
import com.dev.simplespa.module.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private RecaptchaService recaptchaService;

	@MockitoBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	private final String BASE_URL = "/simple-spa";

	// ---------------- LOGIN ----------------

	@Test
	void login_shouldReturnToken_whenValidCredentialsAndCaptchaOk() throws Exception {

		Map<String, String> request = Map.of(
			"username", "user1",
			"password", "pass",
			"captchaToken", "token"
		);

		when(recaptchaService.verify("token")).thenReturn(true);
		when(userService.isAuthentic("user1", "pass")).thenReturn(true);
		when(jwtUtil.generateToken("user1")).thenReturn("jwt-token");

		mockMvc.perform(post(BASE_URL + "/login")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").value("jwt-token"));
	}

	@Test
	void login_shouldReturn403_whenCaptchaFails() throws Exception {

		Map<String, String> request = Map.of(
			"username", "user1",
			"password", "pass",
			"captchaToken", "bad"
		);

		when(recaptchaService.verify("bad")).thenReturn(false);

		mockMvc.perform(post(BASE_URL + "/login")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isForbidden());
	}

	@Test
	void login_shouldReturn401_whenInvalidCredentials() throws Exception {

		Map<String, String> request = Map.of(
			"username", "user1",
			"password", "wrong",
			"captchaToken", "token"
		);

		when(recaptchaService.verify("token")).thenReturn(true);
		when(userService.isAuthentic("user1", "wrong")).thenReturn(false);

		mockMvc.perform(post(BASE_URL + "/login")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void login_shouldBypassCaptcha_whenFlagTrue() throws Exception {

		Map<String, String> request = Map.of(
			"username", "user1",
			"password", "pass",
			"bypassReCAPTCHA", "true"
		);

		when(userService.isAuthentic("user1", "pass")).thenReturn(true);
		when(jwtUtil.generateToken("user1")).thenReturn("jwt-token");

		mockMvc.perform(post(BASE_URL + "/login")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").value("jwt-token"));

		verifyNoInteractions(recaptchaService);
	}

	// ---------------- REGISTER ----------------

	@Test
	void register_shouldCreateUser_whenValidRequest() throws Exception {

		RegistrationRequest request = RegistrationRequest.builder()
			.username("user1")
			.password("pass")
			.captchaToken("token").build();

		when(recaptchaService.verify("token")).thenReturn(true);
		when(userService.findByUsername("user1")).thenReturn(Optional.empty());

		mockMvc.perform(post(BASE_URL + "/register")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("User created"));
	}

	@Test
	void register_shouldReturn403_whenCaptchaFails() throws Exception {

		RegistrationRequest request = RegistrationRequest.builder()
			.username("user1")
			.captchaToken("bad").build();

		when(recaptchaService.verify("bad")).thenReturn(false);

		mockMvc.perform(post(BASE_URL + "/register")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.message").value("reCAPTCHA failed"));
	}

	@Test
	void register_shouldReturn409_whenUsernameExists() throws Exception {

		RegistrationRequest request = RegistrationRequest.builder()
			.username("user1")
			.captchaToken("token").build();

		when(recaptchaService.verify("token")).thenReturn(true);
		when(userService.findByUsername("user1"))
			.thenReturn(Optional.of(User.builder().username("user1").build()));

		mockMvc.perform(post(BASE_URL + "/register")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.message").value("Username already exists"));
	}

	@Test
	void register_shouldBypassCaptcha_whenFlagTrue() throws Exception {

		RegistrationRequest request = RegistrationRequest.builder()
			.username("user1")
			.bypassReCAPTCHA(true).build();

		when(userService.findByUsername("user1")).thenReturn(Optional.empty());

		mockMvc.perform(post(BASE_URL + "/register")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());

		verifyNoInteractions(recaptchaService);
	}
}
