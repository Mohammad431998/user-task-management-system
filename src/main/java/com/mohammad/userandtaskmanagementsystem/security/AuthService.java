package com.mohammad.userandtaskmanagementsystem.security;

import com.mohammad.userandtaskmanagementsystem.dto.LoginRequest;
import com.mohammad.userandtaskmanagementsystem.dto.LoginResponse;
import com.mohammad.userandtaskmanagementsystem.entity.User;
import com.mohammad.userandtaskmanagementsystem.service.ActivityLogService;
import com.mohammad.userandtaskmanagementsystem.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final ActivityLogService activityLogService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserService userService,
            ActivityLogService activityLogService) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.activityLogService = activityLogService;
    }

    public LoginResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );

        String username = authentication.getName();

        User user = userService.getUserByUsername(username);

        String token = jwtService.generateToken(username);

        activityLogService.createLog(
                user.getId(),
                "LOGIN",
                "USER",
                user.getId(),
                user.getName() + " logged in"
        );

        return new LoginResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().getName()
        );
    }

    public void logout(String username) {

        User user = userService.getUserByUsername(username);

        activityLogService.createLog(
                user.getId(),
                "LOGOUT",
                "USER",
                user.getId(),
                user.getName() + " logged out"
        );
    }
}