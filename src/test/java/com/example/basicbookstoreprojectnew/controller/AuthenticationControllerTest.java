package com.example.basicbookstoreprojectnew.controller;

import com.example.basicbookstoreprojectnew.exception.InvalidCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    private static final String EMAIL = "reader@tvoya-knyharnia.ua";
    private static final String PASSWORD = "Secret123";

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void registerUser() throws Exception {
        String registration = """
                {
                  "email": "%s",
                  "password": "%s",
                  "repeatPassword": "%s",
                  "firstName": "Olena",
                  "lastName": "Reader"
                }
                """.formatted(EMAIL, PASSWORD, PASSWORD);

        mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registration))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Login with correct credentials returns a token")
    void login_ValidCredentials_ReturnsToken() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(EMAIL, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("Login with a wrong password returns 401 with a generic message")
    void login_WrongPassword_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(EMAIL, "WrongPass1")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value(InvalidCredentialsException.MESSAGE));
    }

    @Test
    @DisplayName("Login with an unknown email returns the same 401 as a wrong password")
    void login_UnknownEmail_ReturnsSameUnauthorized() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("nobody@tvoya-knyharnia.ua", PASSWORD)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value(InvalidCredentialsException.MESSAGE));
    }

    private static String loginJson(String email, String password) {
        return """
                {"email": "%s", "password": "%s"}
                """.formatted(email, password);
    }
}
