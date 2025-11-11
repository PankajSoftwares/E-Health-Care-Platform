package com.ehealth.authservice.controller;

import com.ehealth.authservice.dto.AuthRequest;
import com.ehealth.authservice.dto.AuthResponse;
import com.ehealth.authservice.dto.RegisterRequest;
import com.ehealth.authservice.model.User;
import com.ehealth.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and user management APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user account")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("username", user.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Refresh JWT token (placeholder for future implementation)")
    public ResponseEntity<Map<String, String>> refresh(@RequestHeader("Authorization") String token) {
        // TODO: Implement token refresh logic
        Map<String, String> response = new HashMap<>();
        response.put("message", "Token refresh not implemented yet");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Retrieve all registered users (admin only)")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = authService.findAllUsers();
        return ResponseEntity.ok(users);
    }
}
