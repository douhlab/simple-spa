package com.dev.simplespa.component.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.dev.simplespa.model.domain.PersonalLeaveRecord;
import com.dev.simplespa.model.domain.PersonalLeaveRecordCreationRequest;
import com.dev.simplespa.module.personalleaverecord.PersonalLeaveRecordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PersonalLeaveRecordController.class)
class PersonalLeaveRecordControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PersonalLeaveRecordService personalLeaveRecordService;

	@Test
	void testCreate() throws Exception {
		// Given
		var json = """
			    {
			    	"userId": 1,
			        "employerId": 2,
			        "description": "Test Name",
			        "dateHappened": "2024-01-01",
			        "numberOfDays": 3
			    }
			""";
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		var jsonObject = mapper.readValue(json, PersonalLeaveRecordCreationRequest.class);

		var expectedPersonalLeaveRecord = PersonalLeaveRecord.builder()
			.description("Test Name")
			.dateHappened(LocalDate.parse("2024-01-01"))
			.createdBy("test@simple.spa")
			.build();
		Mockito.when(personalLeaveRecordService.create(Mockito.any(PersonalLeaveRecordCreationRequest.class)))
			.thenReturn(expectedPersonalLeaveRecord);

		// When & Then
		var result =
			mockMvc.perform(post("/simple-spa/v1/api/personalleaverecords").contentType(MediaType.APPLICATION_JSON)
				.content(json));

		result.andExpect(status().isCreated());
		verify(personalLeaveRecordService).create(Mockito.assertArg(personalLeaveRecord -> Assertions.assertThat(
				personalLeaveRecord)
			.isEqualTo(jsonObject)));
	}

	@Test
	void testFindAll() throws Exception {
		// Given
		List<PersonalLeaveRecord> expectedList =
			List.of(PersonalLeaveRecord.builder().id(1).description("Test Name").build());
		Mockito.when(personalLeaveRecordService.findAll(1)).thenReturn(expectedList);

		// When & Then
		mockMvc.perform(get("/simple-spa/v1/api/personalleaverecords/all").param("userId", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(1))
			.andExpect(jsonPath("$[0].description").value("Test Name"));
	}

	@Test
	void testFindById() throws Exception {
		// Given
		var expected = PersonalLeaveRecord.builder().id(1).description("Test Name").build();
		Mockito.when(personalLeaveRecordService.findById(1)).thenReturn(Optional.of(expected));

		// When & Then
		mockMvc.perform(get("/simple-spa/v1/api/personalleaverecords").param("id", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("id").value(1))
			.andExpect(jsonPath("description").value("Test Name"));
	}

}
