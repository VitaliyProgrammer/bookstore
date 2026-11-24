package com.example.basicbookstoreprojectnew.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CorsConfigurationTest {

    private static final String ALLOWED_ORIGIN = "http://localhost:5173";
    private static final String UNKNOWN_ORIGIN = "https://evil.example.com";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Preflight from the frontend origin passes without a token")
    void preflight_AllowedOrigin_ReturnsCorsHeaders() throws Exception {
        mockMvc.perform(options("/books")
                        .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                        ALLOWED_ORIGIN));
    }

    @Test
    @DisplayName("Preflight from an unknown origin is rejected")
    void preflight_UnknownOrigin_ReturnsForbidden() throws Exception {
        mockMvc.perform(options("/books")
                        .header(HttpHeaders.ORIGIN, UNKNOWN_ORIGIN)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }
}
