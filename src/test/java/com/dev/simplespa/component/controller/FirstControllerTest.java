package com.dev.simplespa.component.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.dev.simplespa.model.domain.MyFirst;
import com.dev.simplespa.module.first.FirstService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(FirstController.class)
public class FirstControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FirstService firstService;

    @Test
    public void testGetFirst() throws Exception {
        // Given
        List<MyFirst> expectedList = List.of(MyFirst.builder().id(1).description("Test Name").build());
        Mockito.when(firstService.getAll(1)).thenReturn(expectedList);

        // When & Then
        mockMvc.perform(get("/first").param("id", "1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(1))
               .andExpect(jsonPath("$[0].description").value("Test Name"));
    }

    @Test
    public void testGetUsers() throws Exception {
        // Given
        List<MyFirst> expectedList = List.of(MyFirst.builder().id(1).description("Test Name").build());
        Mockito.when(firstService.getAll(1)).thenReturn(expectedList);

        // When & Then
        mockMvc.perform(get("/v1/api/users").param("id", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].description").value("Test Name"));
    }

}
