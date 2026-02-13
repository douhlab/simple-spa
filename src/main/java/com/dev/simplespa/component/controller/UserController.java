package com.dev.simplespa.component.controller;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

import com.dev.simplespa.model.domain.User;
import com.dev.simplespa.module.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/v1/api/users")
    @ResponseStatus(value = HttpStatus.CREATED)
    public User create(@RequestBody @Valid User user) {
        return userService.create(user);
    }

    @GetMapping("/v1/api/users/all")
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/v1/api/users")
    public Optional<User> getById(@RequestParam int id) {
        return userService.getById(id);
    }

}
