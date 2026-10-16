package com.nammaarena.auth.service;

import com.nammaarena.auth.dto.*;
import com.nammaarena.auth.entity.User;
import com.nammaarena.auth.enums.Role;
import com.nammaarena.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        
        user = userRepository.save(user);
        String token = jwtService.generateToken(user);
        
        return new AuthResponse(token, user.getId(), user.getName(), user.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid credentials"));
            
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getName(), user.getRole());
    }

    public List<UserResponse> getAllUsersAndOwners() {
        List<Role> roles = Arrays.asList(Role.USER, Role.OWNER);
        return userRepository.findByRoleIn(roles)
            .stream()
            .map(user -> new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole()))
            .collect(Collectors.toList());
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Cannot delete admin user");
        }
        
        userRepository.delete(user);
    }
}