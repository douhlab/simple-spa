package com.dev.simplespa.module.first;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import com.dev.simplespa.dao.FirstDao;
import com.dev.simplespa.model.domain.MyFirst;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class FirstServiceTest {
    @Mock
    private FirstDao firstDao;

    @InjectMocks
    FirstService firstService;

    @Test
    public void getByIdTest() {
        // given
        when(firstDao.findById(1)).thenReturn(
            java.util.Optional.of(MyFirst.builder().id(1).description("test").build())
        );

        // when
        var actual = firstService.getById(1);

        // then
        assertThat(actual).isPresent();
        assertThat(actual.get().getId()).isEqualTo(1);
        assertThat(actual.get().getDescription()).isEqualTo("test");
    }

    @Test
    public void getAllTest() {
        // given
        when(firstDao.findAll()).thenReturn(List.of(
            MyFirst.builder().id(1).description("test1").build(),
            MyFirst.builder().id(2).description("test2").build(),
            MyFirst.builder().id(3).description("test3").build()
        ));

        // when
        List<MyFirst> actual = firstService.getAll(1);

        // then
        assertThat(actual).hasSize(3);
    }
}
