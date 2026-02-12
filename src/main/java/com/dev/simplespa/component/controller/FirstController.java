package com.dev.simplespa.component.controller;

import java.util.List;
import java.util.Optional;


import com.dev.simplespa.model.domain.MyFirst;
import com.dev.simplespa.module.first.FirstService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FirstController {

    private final FirstService firstService;

    @GetMapping("/first")
    public List<MyFirst> getUserName(@RequestParam int id) {
        return firstService.getAll(id);
    }

    @GetMapping("/v1/api/users")
    public Optional<MyFirst> getById(@RequestParam int id) {
        return firstService.getById(id);
    }
}
