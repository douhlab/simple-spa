package com.dev.simplespa.component.controller;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import com.dev.simplespa.model.domain.Employer;
import com.dev.simplespa.module.employer.EmployerService;
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
public class EmployerController {

    private final EmployerService employerService;

    @PostMapping("/v1/api/employers")
    @ResponseStatus(value = HttpStatus.CREATED)
    public Employer create(@RequestBody @Valid Employer employer) {
        return employerService.create(employer);
    }

    @GetMapping("/v1/api/employers/all")
    public List<Employer> findAll() {
        return employerService.findAll();
    }

    @GetMapping("/v1/api/employers")
    public Optional<Employer> findById(@RequestParam @Positive int id) {
        return employerService.findById(id);
    }
}
