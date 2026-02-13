package com.dev.simplespa.module.user;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import com.dev.simplespa.dao.UserDao;
import com.dev.simplespa.model.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserDao userDao;

    @InjectMocks
    UserService userService;

    @Test
    void createTest() {
        // given
        User user = User.builder().description("test").build();
        when(userDao.create(user)).thenReturn(User.builder().id(1).description("test").build());

        // when
        var actual = userService.create(user);

        // then
        assertThat(actual.getId()).isEqualTo(1);
        assertThat(actual.getDescription()).isEqualTo("test");
    }

    @Test
    void getByIdTest() {
        // given
        when(userDao.findById(1)).thenReturn(
            java.util.Optional.of(User.builder().id(1).description("test").build())
        );

        // when
        var actual = userService.getById(1);

        // then
        assertThat(actual).isPresent();
        assertThat(actual.get().getId()).isEqualTo(1);
        assertThat(actual.get().getDescription()).isEqualTo("test");
    }

    @Test
    void getAllTest() {
        // given
        when(userDao.findAll()).thenReturn(List.of(
            User.builder().id(1).description("test1").build(),
            User.builder().id(2).description("test2").build(),
            User.builder().id(3).description("test3").build()
        ));

        // when
        List<User> actual = userService.getAll();

        // then
        assertThat(actual).hasSize(3);
    }
}
