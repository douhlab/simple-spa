package com.dev.simplespa.component.controller;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import com.dev.simplespa.model.domain.PersonalLeaveRecord;
import com.dev.simplespa.model.domain.PersonalLeaveRecordCreationRequest;
import com.dev.simplespa.module.personalleaverecord.PersonalLeaveRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/${application.product}")
@RequiredArgsConstructor
public class PersonalLeaveRecordController {

    private final PersonalLeaveRecordService personalLeaveRecordService;

    @PostMapping("/v1/api/personalleaverecords")
    @ResponseStatus(value = HttpStatus.CREATED)
    public PersonalLeaveRecord create(@RequestBody @Valid PersonalLeaveRecordCreationRequest request) {
        return personalLeaveRecordService.create(request);
    }

    @GetMapping("/v1/api/personalleaverecords/all")
    public List<PersonalLeaveRecord> findAll(@RequestParam @Positive int userId) {
        return personalLeaveRecordService.findAll(userId);
    }

    @GetMapping("/v1/api/personalleaverecords")
    public Optional<PersonalLeaveRecord> findById(@RequestParam @Positive int id) {
        return personalLeaveRecordService.findById(id);
    }
}
