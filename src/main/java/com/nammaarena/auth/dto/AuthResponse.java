package com.nammaarena.auth.dto;

import com.nammaarena.auth.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String username;
    private Role role;
}