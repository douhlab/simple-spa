package com.dev.simplespa.component.controller;

import java.util.Optional;

import com.dev.simplespa.component.controller.dto.UserAssetsDto;
import com.dev.simplespa.component.controller.dto.UserAssetsUpsertRequest;
import com.dev.simplespa.module.userassets.UserAssetsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/${application.product}")
public class UserAssetsController {

	private final UserAssetsService userAssetsService;
	private final CurrentUserService currentUserService;

	@GetMapping("/v1/api/user-assets")
	public Optional<UserAssetsDto> findForCurrentUser() {
		return userAssetsService.findByUserId(currentUserService.getCurrentUserId());
	}

	@PutMapping("/v1/api/user-assets")
	public UserAssetsDto upsert(@RequestBody @Valid UserAssetsUpsertRequest request) {
		return userAssetsService.upsert(currentUserService.getCurrentUserId(), request);
	}
}
