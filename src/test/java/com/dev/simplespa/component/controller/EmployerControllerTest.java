package com.dev.simplespa.component.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.dev.simplespa.model.domain.Employer;
import com.dev.simplespa.module.employer.EmployerService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployerController.class)
class EmployerControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private EmployerService employerService;

	@Test
	void testCreate() throws Exception {
		// Given
		var employerToCreate = """
			    {
			        "name": "Test Name",
			        "startWorkDate": "2024-01-01",
			        "createdBy": "test@simple.spa"
			    }
			""";

		var expectedEmployer = Employer.builder()
			.name("Test Name")
			.startWorkDate(LocalDate.parse("2024-01-01"))
			.createdBy("test@simple.spa")
			.build();
		Mockito.when(employerService.create(Mockito.any(Employer.class))).thenReturn(expectedEmployer);

		// When & Then
		var result = mockMvc.perform(post("/simple-spa/v1/api/employers").contentType(MediaType.APPLICATION_JSON)
			.content(employerToCreate));

		result.andExpect(status().isCreated());
		verify(employerService).create(Mockito.assertArg(employer -> Assertions.assertThat(employer)
			.isEqualTo(expectedEmployer)));
	}

	@Test
	void testFindAll() throws Exception {
		// Given
		List<Employer> expectedList = List.of(Employer.builder().id(1).name("Test Name").build());
		Mockito.when(employerService.findAll()).thenReturn(expectedList);

		// When & Then
		mockMvc.perform(get("/simple-spa/v1/api/employers/all").param("id", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(1))
			.andExpect(jsonPath("$[0].name").value("Test Name"));
	}

	@Test
	void testFindById() throws Exception {
		// Given
		var expected = Employer.builder().id(1).name("Test Name").build();
		Mockito.when(employerService.findById(1)).thenReturn(Optional.of(expected));

		// When & Then
		mockMvc.perform(get("/simple-spa/v1/api/employers").param("id", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("id").value(1))
			.andExpect(jsonPath("name").value("Test Name"));
	}

}
