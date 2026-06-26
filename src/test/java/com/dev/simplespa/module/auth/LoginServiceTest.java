package com.dev.simplespa.module.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import com.dev.simplespa.component.controller.dto.LoginResponse;
import com.dev.simplespa.component.controller.util.JwtUtil;
import com.dev.simplespa.model.domain.LoginEvent;
import com.dev.simplespa.model.domain.LoginStatus;
import com.dev.simplespa.model.domain.User;
import com.dev.simplespa.module.RecaptchaService;
import com.dev.simplespa.module.user.AuthResult;
import com.dev.simplespa.module.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

	@Mock
	private RecaptchaService recaptchaService;

	@Mock
	private UserService userService;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private LoginEventService loginEventService;

	@InjectMocks
	private LoginService loginService;

	@Test
	void login_shouldReturnTokenAndRecordSuccess() {
		var user = User.builder().id(1).username("user1").build();
		var body = Map.of(
			"username", "user1",
			"password", "pass",
			"captchaToken", "valid-token"
		);

		when(recaptchaService.verify("valid-token")).thenReturn(true);
		when(userService.findByUsername("user1")).thenReturn(Optional.of(user));
		when(userService.authenticate("user1", "pass")).thenReturn(AuthResult.SUCCESS);
		when(jwtUtil.generateToken("user1", 1)).thenReturn("jwt-token");

		LoginResponse response = loginService.login(body, "127.0.0.1", "JUnit");

		assertThat(response.getToken()).isEqualTo("jwt-token");
		assertThat(response.getUser()).isEqualTo(user);

		ArgumentCaptor<LoginEvent> captor = ArgumentCaptor.forClass(LoginEvent.class);
		verify(loginEventService).record(captor.capture());
		assertThat(captor.getValue().getStatus()).isEqualTo(LoginStatus.SUCCESS);
		assertThat(captor.getValue().getUserId()).isEqualTo(1);
		assertThat(captor.getValue().getIpAddress()).isEqualTo("127.0.0.1");
	}

	@Test
	void login_shouldRecordCaptchaFailure() {
		var body = Map.of(
			"username", "user1",
			"password", "pass",
			"captchaToken", "bad-token"
		);

		when(recaptchaService.verify("bad-token")).thenReturn(false);

		assertThatThrownBy(() -> loginService.login(body, "127.0.0.1", "JUnit"))
			.isInstanceOf(ResponseStatusException.class)
			.extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
			.isEqualTo(HttpStatus.FORBIDDEN);

		ArgumentCaptor<LoginEvent> captor = ArgumentCaptor.forClass(LoginEvent.class);
		verify(loginEventService).record(captor.capture());
		assertThat(captor.getValue().getStatus()).isEqualTo(LoginStatus.CAPTCHA_FAILED);
		verify(userService, never()).authenticate(any(), any());
	}

	@Test
	void login_shouldRecordUserNotFound() {
		var body = Map.of(
			"username", "unknown",
			"password", "pass",
			"captchaToken", "valid-token"
		);

		when(recaptchaService.verify("valid-token")).thenReturn(true);
		when(userService.findByUsername("unknown")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> loginService.login(body, "127.0.0.1", "JUnit"))
			.isInstanceOf(ResponseStatusException.class)
			.extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
			.isEqualTo(HttpStatus.UNAUTHORIZED);

		ArgumentCaptor<LoginEvent> captor = ArgumentCaptor.forClass(LoginEvent.class);
		verify(loginEventService).record(captor.capture());
		assertThat(captor.getValue().getStatus()).isEqualTo(LoginStatus.USER_NOT_FOUND);
	}

	@Test
	void login_shouldRecordInvalidCredentials() {
		var user = User.builder().id(1).username("user1").build();
		var body = Map.of(
			"username", "user1",
			"password", "wrong",
			"captchaToken", "valid-token"
		);

		when(recaptchaService.verify("valid-token")).thenReturn(true);
		when(userService.findByUsername("user1")).thenReturn(Optional.of(user));
		when(userService.authenticate("user1", "wrong")).thenReturn(AuthResult.INVALID_PASSWORD);

		assertThatThrownBy(() -> loginService.login(body, "127.0.0.1", "JUnit"))
			.isInstanceOf(ResponseStatusException.class)
			.extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
			.isEqualTo(HttpStatus.UNAUTHORIZED);

		ArgumentCaptor<LoginEvent> captor = ArgumentCaptor.forClass(LoginEvent.class);
		verify(loginEventService).record(captor.capture());
		assertThat(captor.getValue().getStatus()).isEqualTo(LoginStatus.INVALID_CREDENTIALS);
		assertThat(captor.getValue().getUserId()).isEqualTo(1);
	}

	@Test
	void login_shouldBypassCaptchaWhenFlagIsTrue() {
		var user = User.builder().id(1).username("user1").build();
		var body = Map.of(
			"username", "user1",
			"password", "pass",
			"bypassReCAPTCHA", "true"
		);

		when(userService.findByUsername("user1")).thenReturn(Optional.of(user));
		when(userService.authenticate("user1", "pass")).thenReturn(AuthResult.SUCCESS);
		when(jwtUtil.generateToken("user1", 1)).thenReturn("jwt-token");

		LoginResponse response = loginService.login(body, "127.0.0.1", "JUnit");

		assertThat(response.getToken()).isEqualTo("jwt-token");
		verify(recaptchaService, never()).verify(any());
		verify(loginEventService).record(any(LoginEvent.class));
	}
}
