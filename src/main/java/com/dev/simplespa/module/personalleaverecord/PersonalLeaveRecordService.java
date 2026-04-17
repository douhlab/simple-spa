package com.dev.simplespa.module.personalleaverecord;

import java.util.List;
import java.util.Optional;

import com.dev.simplespa.dao.PersonalLeaveRecordDao;
import com.dev.simplespa.model.domain.PersonalLeaveRecord;
import com.dev.simplespa.model.domain.PersonalLeaveRecordCreationRequest;
import com.dev.simplespa.module.employer.EmployerService;
import com.dev.simplespa.module.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonalLeaveRecordService {

    private final PersonalLeaveRecordDao personalLeaveRecordDao;
    private final EmployerService employerService;
    private final UserService userService;

    public PersonalLeaveRecord create(PersonalLeaveRecordCreationRequest request) {
        var created = personalLeaveRecordDao.create(request);
        return Optional.ofNullable(created).map(this::enrich).orElseThrow(() -> new RuntimeException(
            "Failed to enrich created PersonalLeaveRecord: " + created));
    }

    public Optional<PersonalLeaveRecord> findById(int id) {
        return personalLeaveRecordDao.findById(id).map(this::enrich);
    }

    public List<PersonalLeaveRecord> findAll(int userId) {
        return personalLeaveRecordDao.findAll(userId).stream().map(this::enrich).toList();
    }

    public void deleteById(int id) {
        personalLeaveRecordDao.deleteById(id);
    }

    private PersonalLeaveRecord enrich(PersonalLeaveRecord personalLeaveRecord) {
        var employer = employerService.findById(personalLeaveRecord.getEmployer().getId()).orElse(null);
        var user = userService.findById(personalLeaveRecord.getUser().getId()).orElse(null);
        return personalLeaveRecord.toBuilder()
            .employer(employer)
            .user(user)
            .build();
    }
}
