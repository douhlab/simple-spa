package com.dev.simplespa.module.personalleaverecord;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.dev.simplespa.dao.PersonalLeaveRecordDao;
import com.dev.simplespa.model.domain.Employer;
import com.dev.simplespa.model.domain.PersonalLeaveRecord;
import com.dev.simplespa.model.domain.PersonalLeaveRecordCreationRequest;
import com.dev.simplespa.model.domain.User;
import com.dev.simplespa.module.employer.EmployerService;
import com.dev.simplespa.module.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class PersonalLeaveRecordServiceTest {
    @Mock
    private PersonalLeaveRecordDao personalLeaveRecordDao;

    @Mock
    private EmployerService employerService;

    @Mock
    private UserService userService;

    @InjectMocks
	PersonalLeaveRecordService personalLeaveRecordService;

    @Test
    void createTest() {
        // given
        var userId = 1;
        var employerId = 2;
        var numberOfDays =3;
        var balance = 4;
        var description = "myLeaveRequest";

        var personalLeaveRecordCreationRequest = PersonalLeaveRecordCreationRequest.builder()
            .userId(userId)
            .employerId(employerId)
            .numberOfDays(numberOfDays)
            .currentBalance(balance)
            .description(description)
            .build();

        var personalLeaveRecord = PersonalLeaveRecord.builder()
            .user(User.builder().id(userId).build())
            .employer(Employer.builder().id(employerId).build())
            .description(description)
            .numberOfDays(numberOfDays)
            .currentBalance(balance)
            .build();
        when(personalLeaveRecordDao.create(any())).thenReturn(personalLeaveRecord);

        var user = User.builder().id(userId).name("test-user").build();
        when(userService.findById(anyInt())).thenReturn(Optional.of(user));

        var employer = Employer.builder().id(employerId).name("test-employer").build();
        when(employerService.findById(anyInt())).thenReturn(Optional.of(employer));

        // when
        var actual = personalLeaveRecordService.create(personalLeaveRecordCreationRequest);

        // then
        assertThat(actual).isNotNull();
    }

    @Test
    void findByIdTest() {
        // given
        var userId = 1;
        var employerId = 2;
        var numberOfDays =3;
        var balance = 4;
        var description = "myLeaveRequest";

        var personalLeaveRecord = PersonalLeaveRecord.builder()
            .user(User.builder().id(userId).build())
            .employer(Employer.builder().id(employerId).build())
            .description(description)
            .numberOfDays(numberOfDays)
            .currentBalance(balance)
            .build();
        when(personalLeaveRecordDao.findById(anyInt())).thenReturn(Optional.of(personalLeaveRecord));

        var user = User.builder().id(userId).name("test-user").build();
        when(userService.findById(anyInt())).thenReturn(Optional.of(user));

        var employer = Employer.builder().id(employerId).name("test-employer").build();
        when(employerService.findById(anyInt())).thenReturn(Optional.of(employer));

        // when
        var actual = personalLeaveRecordService.findById(1);

        // then
        assertThat(actual).isPresent();
    }

    @Test
    void findAllTest() {
        // given
        var userId = 1;
        var employerId = 2;
        var numberOfDays =3;
        var balance = 4;

        when(personalLeaveRecordDao.findAll(anyInt())).thenReturn(List.of(
            PersonalLeaveRecord.builder()
                .user(User.builder().id(userId).build())
                .employer(Employer.builder().id(employerId).build())
                .description("desc1")
                .numberOfDays(numberOfDays)
                .currentBalance(balance)
                .build(),
        PersonalLeaveRecord.builder()
            .user(User.builder().id(userId).build())
            .employer(Employer.builder().id(employerId).build())
            .description("desc2")
            .numberOfDays(numberOfDays)
            .currentBalance(balance)
            .build(),
        PersonalLeaveRecord.builder()
            .user(User.builder().id(userId).build())
            .employer(Employer.builder().id(employerId).build())
            .description("desc3")
            .numberOfDays(numberOfDays)
            .currentBalance(balance)
            .build()
        ));

        // when
        List<PersonalLeaveRecord> actual = personalLeaveRecordService.findAll(userId);

        // then
        assertThat(actual).hasSize(3);
    }

    @Test
    void findDeleteById() {
        // given
        var leaveRecordId = 1;

        // when
        personalLeaveRecordService.deleteById(leaveRecordId);

        // then
        verify(personalLeaveRecordDao).deleteById(leaveRecordId);
    }

}
