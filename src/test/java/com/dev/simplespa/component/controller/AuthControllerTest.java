package com.dev.simplespa.component.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.Optional;

import com.dev.simplespa.component.controller.dto.LoginResponse;
import com.dev.simplespa.component.controller.dto.RegistrationRequest;
import com.dev.simplespa.model.domain.User;
import com.dev.simplespa.module.RecaptchaService;
import com.dev.simplespa.module.auth.LoginService;
import com.dev.simplespa.module.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;


@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private LoginService loginService;

	@MockitoBean
	private RecaptchaService recaptchaService;

	@MockitoBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	private final String BASE_URL = "/simple-spa";

	@Test
	void shouldLoginSuccessfully() throws Exception {
		var user = User.builder().id(1).username("user1").build();
		var loginResponse = LoginResponse.builder().token("jwt-token").user(user).build();

		Map<String, String> body = Map.of(
			"username", "user1",
			"password", "pass",
			"captchaToken", "valid-token"
		);

		when(loginService.login(eq(body), any(), any())).thenReturn(loginResponse);

		mockMvc.perform(post(BASE_URL + "/login")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(body)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").value("jwt-token"))
			.andExpect(jsonPath("$.user.username").value("user1"));
	}

	@Test
	void shouldReturn403_whenCaptchaFails() throws Exception {
		Map<String, String> body = Map.of(
			"username", "user1",
			"password", "pass",
			"captchaToken", "bad-token"
		);

		when(loginService.login(eq(body), any(), any()))
			.thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "reCAPTCHA failed"));

		mockMvc.perform(post(BASE_URL + "/login")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(body)))
			.andExpect(status().isForbidden())
			.andExpect(status().reason("reCAPTCHA failed"));
	}

	@Test
	void shouldReturn401_whenInvalidCredentials() throws Exception {
		Map<String, String> body = Map.of(
			"username", "user1",
			"password", "wrong",
			"captchaToken", "valid-token"
		);

		when(loginService.login(eq(body), any(), any()))
			.thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

		mockMvc.perform(post(BASE_URL + "/login")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(body)))
			.andExpect(status().isUnauthorized())
			.andExpect(status().reason("Invalid credentials"));
	}

	@Test
	void shouldReturn401_whenUserNotFound() throws Exception {
		Map<String, String> body = Map.of(
			"username", "user1",
			"password", "pass",
			"captchaToken", "valid-token"
		);

		when(loginService.login(eq(body), any(), any()))
			.thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

		mockMvc.perform(post(BASE_URL + "/login")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(body)))
			.andExpect(status().isUnauthorized())
			.andExpect(status().reason("Invalid credentials"));
	}

	@Test
	void shouldBypassCaptcha_whenFlagIsTrue() throws Exception {
		var user = User.builder().id(1).username("user1").build();
		var loginResponse = LoginResponse.builder().token("jwt-token").user(user).build();

		Map<String, String> body = Map.of(
			"username", "user1",
			"password", "pass",
			"bypassReCAPTCHA", "true"
		);

		when(loginService.login(eq(body), any(), any())).thenReturn(loginResponse);

		mockMvc.perform(post(BASE_URL + "/login")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(body)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").value("jwt-token"));
	}

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
