package com.dev.simplespa.component.controller;

import java.util.Map;

import com.dev.simplespa.component.controller.util.JwtUtil;
import com.dev.simplespa.module.RecaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/${application.product}")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RecaptchaService recaptchaService;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> body) {

        // 1️⃣ Verify reCAPTCHA
        boolean captchaOk = recaptchaService.verify(body.get("captchaToken"));
        if (!captchaOk) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "reCAPTCHA failed");
        }

        String username = body.get("username");
        String password = body.get("password");

        // For lab: simple validation
        if("labuser".equals(username) && "labpass".equals(password)) {
            String token = jwtUtil.generateToken(username);
            return Map.of("token", token);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }
}
