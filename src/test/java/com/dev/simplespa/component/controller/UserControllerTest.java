package com.dev.simplespa.component.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import com.dev.simplespa.model.domain.User;
import com.dev.simplespa.module.user.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@Test
	void testFindAll() throws Exception {
		// Given
		List<User> expectedList = List.of(User.builder().id(1).name("Test Name").build());
		Mockito.when(userService.findAll()).thenReturn(expectedList);

		// When & Then
		mockMvc.perform(get("/simple-spa/v1/api/users/all").param("id", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(1))
			.andExpect(jsonPath("$[0].name").value("Test Name"));
	}

	@Test
	void testFindById() throws Exception {
		// Given
		var expected = User.builder().id(1).name("Test Name").build();
		Mockito.when(userService.findById(1)).thenReturn(Optional.of(expected));

		// When & Then
		mockMvc.perform(get("/simple-spa/v1/api/users").param("id", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("id").value(1))
			.andExpect(jsonPath("name").value("Test Name"));
	}

}
