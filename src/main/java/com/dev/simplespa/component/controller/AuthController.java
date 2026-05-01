package com.dev.simplespa.component.controller;

import java.util.Map;
import java.util.Optional;

import com.dev.simplespa.component.controller.dto.LoginResponse;
import com.dev.simplespa.component.controller.util.JwtUtil;
import com.dev.simplespa.component.controller.dto.RegistrationRequest;
import com.dev.simplespa.module.RecaptchaService;
import com.dev.simplespa.module.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/${application.product}")
@RequiredArgsConstructor
public class AuthController {

	private final JwtUtil jwtUtil;
	private final RecaptchaService recaptchaService;
	private final UserService userService;

	@PostMapping("/login")
	public LoginResponse login(@RequestBody Map<String, String> body) {

		boolean bypassReCAPTCHA =
			Optional.ofNullable(body.get("bypassReCAPTCHA")).map(Boolean::parseBoolean).orElse(false);
		if (!bypassReCAPTCHA) {
			// 1️⃣ Verify reCAPTCHA
			boolean captchaOk = recaptchaService.verify(body.get("captchaToken"));
			if (!captchaOk) {
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, "reCAPTCHA failed");
			}
		}

		String username = body.get("username");
		String rawPassword = body.get("password");

		if (userService.isAuthentic(username, rawPassword)) {
			var user = userService.findByUsername(username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
					"Problem fetching user details"));
			String token = jwtUtil.generateToken(username, user.getId());
			return LoginResponse.builder().token(token).user(user).build();
		} else {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
		}
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
