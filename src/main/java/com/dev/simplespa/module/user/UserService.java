package com.dev.simplespa.module.user;

import java.util.List;
import java.util.Optional;

import com.dev.simplespa.dao.UserDao;
import com.dev.simplespa.component.controller.dto.RegistrationRequest;
import com.dev.simplespa.model.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final UserDao userDao;
	private final PasswordEncoder passwordEncoder;

	public User register(RegistrationRequest request) {
		User user =
			User.builder()
				.username(request.getUsername())
				.email(request.getEmail())
				.name(request.getName())
				.lastName(request.getLastName())
				.phoneNumber(request.getPhoneNumber())
				.passwordHash(passwordEncoder.encode(request.getPassword()))
				.build();
		return userDao.register(user);
	}

	public List<User> findAll() {
		return userDao.findAll();
	}

	public Optional<User> findById(int id) {
		return userDao.findById(id);
	}

	public Optional<User> findByUsername(String username) {
		return userDao.findAll().stream().filter(user -> user.getUsername().equals(username)).findFirst();
	}

	public boolean isAuthentic(String username, String rawPassword) {
		return findAll().stream()
			.filter(user -> user.getUsername().equals(username))
			.findFirst()
			.map(user -> {
				if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
					log.error("Invalid password for user: {}", username);
					return false;
				}
				return true;
			}).orElseGet(() -> {
				log.error("User not found with username: {}", username);
				return false;
			});
	}
}
