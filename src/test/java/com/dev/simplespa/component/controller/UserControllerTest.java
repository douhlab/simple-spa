package com.dev.simplespa.component.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import com.dev.simplespa.model.domain.User;
import com.dev.simplespa.module.user.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(UserController.class)
class UserControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@Test
	void testCreate() throws Exception {
		// Given
		var userToCreate = """
			    {
			        "name": "Test Name",
			        "createdBy": "test@simple.spa"
			    }
			""";

		var expectedUser = User.builder().name("Test Name").createdBy("test@simple.spa").build();
		Mockito.when(userService.create(Mockito.any(User.class))).thenReturn(expectedUser);

		// When & Then
		var result = mockMvc.perform(post("/simple-spa/v1/api/users").contentType(MediaType.APPLICATION_JSON)
			.content(userToCreate));

		result.andExpect(status().isCreated());
		verify(userService).create(Mockito.assertArg(user -> Assertions.assertThat(user).isEqualTo(expectedUser)));
	}

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
