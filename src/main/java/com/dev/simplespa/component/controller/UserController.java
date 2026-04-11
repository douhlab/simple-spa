package com.dev.simplespa.component.controller;

import java.util.List;
import java.util.Optional;

import com.dev.simplespa.model.domain.User;
import com.dev.simplespa.module.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/${application.product}")
public class UserController {

    private final UserService userService;

    @GetMapping("/v1/api/users/all")
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/v1/api/users")
    public Optional<User> findById(@RequestParam int id) {
        return userService.findById(id);
    }

}
