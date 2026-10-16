package com.nammaarena.auth.service;

import com.nammaarena.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    
    private final SecretKey key = Keys.hmacShaKeyFor("nammaarena-secret-key-for-jwt-token".getBytes(StandardCharsets.UTF_8));
    private final long EXPIRATION_TIME = 86400000; // 24 hours

    public String generateToken(User user) {
        return Jwts.builder()
            .setSubject(user.getId().toString())
            .claim("userId", user.getId())
            .claim("username", user.getName())
            .claim("role", user.getRole().name())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(key)
            .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}