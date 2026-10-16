package com.nammaarena.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nammaarena.auth.dto.*;
import com.nammaarena.auth.enums.Role;
import com.nammaarena.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setRole(Role.USER);

        AuthResponse response = new AuthResponse("token", 1L, "Test User", Role.USER);
        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        AuthResponse response = new AuthResponse("token", 1L, "Test User", Role.USER);
        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsersAndOwners_Success() throws Exception {
        List<UserResponse> users = Arrays.asList(
            new UserResponse(1L, "User1", "user1@example.com", Role.USER),
            new UserResponse(2L, "Owner1", "owner1@example.com", Role.OWNER)
        );
        when(authService.getAllUsersAndOwners()).thenReturn(users);

        mockMvc.perform(get("/api/auth/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_Success() throws Exception {
        doNothing().when(authService).deleteUser(1L);

        mockMvc.perform(delete("/api/auth/users/1")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(authService).deleteUser(1L);
    }
}