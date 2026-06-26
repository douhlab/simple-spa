package com.dev.simplespa.module.auth;

import java.time.LocalDateTime;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

	private final RecaptchaService recaptchaService;
	private final UserService userService;
	private final JwtUtil jwtUtil;
	private final LoginEventService loginEventService;

	public LoginResponse login(Map<String, String> body, String ipAddress, String userAgent) {
		String username = body.get("username");
		String rawPassword = body.get("password");
		boolean bypassReCAPTCHA =
			Optional.ofNullable(body.get("bypassReCAPTCHA")).map(Boolean::parseBoolean).orElse(false);

		if (!bypassReCAPTCHA) {
			boolean captchaOk = recaptchaService.verify(body.get("captchaToken"));
			log.info("Authentication - captchaCheckResult: {}", captchaOk);
			if (!captchaOk) {
				loginEventService.record(buildEvent(username, null, LoginStatus.CAPTCHA_FAILED,
					"reCAPTCHA failed", ipAddress, userAgent));
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, "reCAPTCHA failed");
			}
		}

		Optional<User> userOpt = userService.findByUsername(username);
		if (userOpt.isEmpty()) {
			loginEventService.record(buildEvent(username, null, LoginStatus.USER_NOT_FOUND,
				"User not found", ipAddress, userAgent));
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
		}

		User user = userOpt.get();
		AuthResult authResult = userService.authenticate(username, rawPassword);
		if (authResult == AuthResult.INVALID_PASSWORD) {
			loginEventService.record(buildEvent(username, user.getId(), LoginStatus.INVALID_CREDENTIALS,
				"Invalid password", ipAddress, userAgent));
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
		}

		String token = jwtUtil.generateToken(username, user.getId());
		loginEventService.record(buildEvent(username, user.getId(), LoginStatus.SUCCESS,
			null, ipAddress, userAgent));

		return LoginResponse.builder().token(token).user(user).build();
	}

	private LoginEvent buildEvent(String username, Integer userId, LoginStatus status,
		String failureReason, String ipAddress, String userAgent) {
		return LoginEvent.builder()
			.userId(userId)
			.username(username)
			.status(status)
			.failureReason(failureReason)
			.ipAddress(ipAddress)
			.userAgent(userAgent)
			.eventDate(LocalDateTime.now())
			.createdBy(username)
			.build();
	}
}
