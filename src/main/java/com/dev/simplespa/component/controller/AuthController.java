package com.dev.simplespa.component.controller;

import java.util.Map;
import java.util.Optional;

import com.dev.simplespa.component.controller.dto.LoginResponse;
import com.dev.simplespa.component.controller.dto.RegistrationRequest;
import com.dev.simplespa.module.RecaptchaService;
import com.dev.simplespa.module.auth.LoginService;
import com.dev.simplespa.module.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/${application.product}")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

	private final LoginService loginService;
	private final RecaptchaService recaptchaService;
	private final UserService userService;

	@PostMapping("/login")
	public LoginResponse login(@RequestBody Map<String, String> body, HttpServletRequest request) {
		return loginService.login(body, request.getRemoteAddr(), request.getHeader("User-Agent"));
	}

	@PostMapping("/register")
	public ResponseEntity<Map<String, String>> register(@RequestBody RegistrationRequest request) {
		boolean bypassReCAPTCHA = Optional.ofNullable(request.getBypassReCAPTCHA()).orElse(false);
		if (!bypassReCAPTCHA) {
			// 1️⃣ Verify reCAPTCHA
			boolean captchaOk = recaptchaService.verify(request.getCaptchaToken());
			if (!captchaOk) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "reCAPTCHA failed"));
			}
		}

		if (userService.findByUsername(request.getUsername()).isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Username already exists"));
		}
		userService.register(request);

		return ResponseEntity.ok(Map.of("message", "User created"));
	}
}
