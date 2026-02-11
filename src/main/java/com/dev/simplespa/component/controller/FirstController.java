package com.dev.simplespa.component.controller;

import java.util.List;

import com.dev.simplespa.model.domain.MyFirst;
import com.dev.simplespa.module.first.FirstService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FirstController {

    private final FirstService firstService;

    public FirstController(FirstService firstService) {
        this.firstService = firstService;
    }

    @GetMapping("/first")
    public List<MyFirst> getUserName(@RequestParam int id) {
        return firstService.getAll(id);
    }
}
