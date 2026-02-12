package com.dev.simplespa.component.context;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(CorsConfig.class)
public class CorsConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCorsHeaders() throws Exception {
        // A simple controller mapping (e.g., "/") is needed for this test to work.
        // Assuming there is a GET endpoint available.
        mockMvc.perform(MockMvcRequestBuilders.get("/first")
                .header("Origin", "http://example.com"));
    }

    @Test
    public void testCorsPreflightHeaders() throws Exception {
        // Test a CORS preflight request (OPTIONS method)
        mockMvc.perform(MockMvcRequestBuilders.options("/v1/api/users") // Use any valid path
                .header("Origin", "http://example.com")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
