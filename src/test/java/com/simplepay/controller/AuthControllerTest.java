package com.simplepay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnJwtTokenForValidCredentials() throws Exception {
        Map<String, String> authRequest = Map.of(
                "clientId", "simplepay-client",
                "clientSecret", "simplepay-secret-key-2025"
        );

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.access_token").isNotEmpty())
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").exists())
                .andExpect(jsonPath("$.scope").value("api:read api:write"));
    }

    @Test
    void shouldReturnBadRequestForMissingClientId() throws Exception {
        Map<String, String> authRequest = Map.of("clientSecret", "simplepay-secret-key-2025");

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Client ID and client secret required"));
    }

    @Test
    void shouldReturnBadRequestForMissingClientSecret() throws Exception {
        Map<String, String> authRequest = Map.of("clientId", "simplepay-client");

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Client ID and client secret required"));
    }

    @Test
    void shouldReturnBadRequestForEmptyCredentials() throws Exception {
        Map<String, String> authRequest = Map.of();

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Client ID and client secret required"));
    }

    @Test
    void shouldReturnBadRequestForInvalidCredentials() throws Exception {
        Map<String, String> authRequest = Map.of(
                "clientId", "invalid-client",
                "clientSecret", "invalid-secret"
        );

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid client credentials"));
    }
}
