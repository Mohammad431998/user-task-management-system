package com.mohammad.userandtaskmanagementsystem.controller;


import com.mohammad.userandtaskmanagementsystem.dto.LoginRequest;
import com.mohammad.userandtaskmanagementsystem.dto.LoginResponse;
import com.mohammad.userandtaskmanagementsystem.security.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            Authentication authentication) {

        if (authentication != null) {
            authService.logout(authentication.getName());
        }

        return ResponseEntity.noContent().build();
    }
}