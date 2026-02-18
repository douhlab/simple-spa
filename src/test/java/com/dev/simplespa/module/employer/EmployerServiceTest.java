package com.dev.simplespa.module.employer;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import com.dev.simplespa.dao.EmployerDao;
import com.dev.simplespa.model.domain.Employer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class EmployerServiceTest {
    @Mock
    private EmployerDao employerDao;

    @InjectMocks
    EmployerService employerService;

    @Test
    void createTest() {
        // given
        Employer employer = Employer.builder().name("test").build();
        when(employerDao.create(employer)).thenReturn(Employer.builder().id(1).name("test").build());

        // when
        var actual = employerService.create(employer);

        // then
        assertThat(actual.getId()).isEqualTo(1);
        assertThat(actual.getName()).isEqualTo("test");
    }

    @Test
    void findByIdTest() {
        // given
        when(employerDao.findById(1)).thenReturn(
            java.util.Optional.of(Employer.builder().id(1).name("test").build())
        );

        // when
        var actual = employerService.findById(1);

        // then
        assertThat(actual).isPresent();
        assertThat(actual.get().getId()).isEqualTo(1);
        assertThat(actual.get().getName()).isEqualTo("test");
    }

    @Test
    void findAllTest() {
        // given
        when(employerDao.findAll()).thenReturn(List.of(
            Employer.builder().id(1).name("test1").build(),
            Employer.builder().id(2).name("test2").build(),
            Employer.builder().id(3).name("test3").build()
        ));

        // when
        List<Employer> actual = employerService.findAll();

        // then
        assertThat(actual).hasSize(3);
    }
}
