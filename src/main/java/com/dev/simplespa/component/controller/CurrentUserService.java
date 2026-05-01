package com.dev.simplespa.component.controller;

import com.dev.simplespa.component.controller.dto.UserPrincipal;
import com.dev.simplespa.model.domain.User;
import com.dev.simplespa.module.user.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserService {

    private final UserService userService;

    public CurrentUserService(UserService userService) {
        this.userService = userService;
    }

    public User getCurrentUser() {
        var principal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return userService.findByUsername(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public int getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
