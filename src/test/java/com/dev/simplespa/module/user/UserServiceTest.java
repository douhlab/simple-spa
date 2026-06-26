package com.dev.simplespa.module.user;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.dev.simplespa.dao.UserDao;
import com.dev.simplespa.component.controller.dto.RegistrationRequest;
import com.dev.simplespa.model.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserDao userDao;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	@Test
	void register_shouldEncodePasswordAndSaveUser() {
		RegistrationRequest request = RegistrationRequest.builder()
			.username("user1")
			.email("test@mail.com")
			.password("rawPass")
			.name("John")
			.lastName("Doe")
			.phoneNumber("123").build();

		when(passwordEncoder.encode("rawPass")).thenReturn("hashed");

		when(userDao.register(any(User.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		User result = userService.register(request);

		assertThat(result.getUsername()).isEqualTo("user1");
		assertThat(result.getPasswordHash()).isEqualTo("hashed");

		verify(passwordEncoder).encode("rawPass");
		verify(userDao).register(any(User.class));
	}

	@Test
	void findAll_shouldReturnUsers() {
		when(userDao.findAll()).thenReturn(List.of(
			User.builder().id(1).username("u1").build(),
			User.builder().id(2).username("u2").build()
		));

		List<User> result = userService.findAll();

		assertThat(result).hasSize(2);
	}

	@Test
	void findById_shouldReturnUser() {
		when(userDao.findById(1))
			.thenReturn(Optional.of(User.builder().id(1).username("u1").build()));

		Optional<User> result = userService.findById(1);

		assertThat(result).isPresent();
		assertThat(result.get().getUsername()).isEqualTo("u1");
	}

	@Test
	void findByUsername_shouldReturnUser() {
		when(userDao.findAll()).thenReturn(List.of(
			User.builder().username("u1").build(),
			User.builder().username("u2").build()
		));

		Optional<User> result = userService.findByUsername("u2");

		assertThat(result).isPresent();
		assertThat(result.get().getUsername()).isEqualTo("u2");
	}

	@Test
	void findByUsername_shouldReturnEmptyWhenNotFound() {
		when(userDao.findAll()).thenReturn(List.of());

		Optional<User> result = userService.findByUsername("unknown");

		assertThat(result).isEmpty();
	}

	@Test
	void isAuthentic_shouldReturnTrueWhenPasswordMatches() {
		User user = User.builder()
			.username("user1")
			.passwordHash("hashed")
			.build();

		when(userDao.findAll()).thenReturn(List.of(user));
		when(passwordEncoder.matches("raw", "hashed")).thenReturn(true);

		boolean result = userService.isAuthentic("user1", "raw");

		assertThat(result).isTrue();
	}

	@Test
	void isAuthentic_shouldReturnFalseWhenPasswordInvalid() {
		User user = User.builder()
			.username("user1")
			.passwordHash("hashed")
			.build();

		when(userDao.findAll()).thenReturn(List.of(user));
		when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

		boolean result = userService.isAuthentic("user1", "wrong");

		assertThat(result).isFalse();
	}

	@Test
	void isAuthentic_shouldReturnFalseWhenUserNotFound() {
		when(userDao.findAll()).thenReturn(List.of());

		boolean result = userService.isAuthentic("unknown", "pass");

		assertThat(result).isFalse();
	}

	@Test
	void authenticate_shouldReturnSuccessWhenPasswordMatches() {
		User user = User.builder()
			.username("user1")
			.passwordHash("hashed")
			.build();

		when(userDao.findAll()).thenReturn(List.of(user));
		when(passwordEncoder.matches("raw", "hashed")).thenReturn(true);

		assertThat(userService.authenticate("user1", "raw")).isEqualTo(AuthResult.SUCCESS);
	}

	@Test
	void authenticate_shouldReturnInvalidPasswordWhenPasswordDoesNotMatch() {
		User user = User.builder()
			.username("user1")
			.passwordHash("hashed")
			.build();

		when(userDao.findAll()).thenReturn(List.of(user));
		when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

		assertThat(userService.authenticate("user1", "wrong")).isEqualTo(AuthResult.INVALID_PASSWORD);
	}

	@Test
	void authenticate_shouldReturnUserNotFoundWhenUserDoesNotExist() {
		when(userDao.findAll()).thenReturn(List.of());

		assertThat(userService.authenticate("unknown", "pass")).isEqualTo(AuthResult.USER_NOT_FOUND);
	}
}
