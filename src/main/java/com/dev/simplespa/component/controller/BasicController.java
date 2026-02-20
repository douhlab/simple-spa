package com.dev.simplespa.component.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BasicController {

	@GetMapping("/")
	public String home() {
		return "Welcome to Simple SPA Backend!";
	}

	@GetMapping("/version")
	public String version() {
		return "v1.0.0"; // you can also read this from application.properties
	}
}
