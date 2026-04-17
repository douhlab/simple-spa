package com.dev.simplespa.component.controller.dto;

import com.dev.simplespa.model.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String token;
    private User user;
}
