package com.nammaarena.auth.service;

import com.nammaarena.auth.dto.LoginRequest;
import com.nammaarena.auth.dto.RegisterRequest;
import com.nammaarena.auth.entity.User;
import com.nammaarena.auth.enums.Role;
import com.nammaarena.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.USER);

        registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");
        registerRequest.setRole(Role.USER);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");
    }

    @Test
    void register_Success() {
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(testUser)).thenReturn("jwt-token");

        var response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_EmailExists_ThrowsException() {
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(testUser));

        assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword())).thenReturn(true);
        when(jwtService.generateToken(testUser)).thenReturn("jwt-token");

        var response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void login_InvalidCredentials_ThrowsException() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
    }

    @Test
    void getAllUsersAndOwners_Success() {
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@example.com");
        user.setRole(Role.USER);

        User owner = new User();
        owner.setId(2L);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner.setRole(Role.OWNER);

        when(userRepository.findByRoleIn(any())).thenReturn(Arrays.asList(user, owner));

        var response = authService.getAllUsersAndOwners();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("User", response.get(0).getName());
        assertEquals("Owner", response.get(1).getName());
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        authService.deleteUser(1L);

        verify(userRepository).delete(testUser);
    }

    @Test
    void deleteUser_UserNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.deleteUser(1L));
        verify(userRepository, never()).delete(any());
    }

    @Test
    void deleteUser_AdminUser_ThrowsException() {
        testUser.setRole(Role.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        assertThrows(RuntimeException.class, () -> authService.deleteUser(1L));
        verify(userRepository, never()).delete(any());
    }

    @Test
    void getAllUsersAndOwners_EmptyList() {
        when(userRepository.findByRoleIn(any())).thenReturn(Arrays.asList());

        var response = authService.getAllUsersAndOwners();

        assertNotNull(response);
        assertEquals(0, response.size());
    }
}