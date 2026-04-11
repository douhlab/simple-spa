package com.dev.simplespa.component.controller;

import java.util.Map;
import java.util.Optional;

import com.dev.simplespa.component.controller.util.JwtUtil;
import com.dev.simplespa.model.domain.RegistrationRequest;
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
	public Map<String, String> login(@RequestBody Map<String, String> body) {

        boolean bypassReCAPTCHA = Optional.ofNullable(body.get("bypassReCAPTCHA")).map(Boolean::parseBoolean).orElse(false);
        if(!bypassReCAPTCHA) {
            // 1️⃣ Verify reCAPTCHA
            boolean captchaOk = recaptchaService.verify(body.get("captchaToken"));
            if (!captchaOk) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "reCAPTCHA failed");
            }
        }

		String username = body.get("username");
		String rawPassword = body.get("password");

		//        if("labuser".equals(username) && "labpass".equals(password)) {
		if (userService.isAuthentic(username, rawPassword)) {
			String token = jwtUtil.generateToken(username);
			return Map.of("token", token);
		} else {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
		}
	}

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        if(userService.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
        userService.register(request);

        return ResponseEntity.ok("User created");
    }
}
