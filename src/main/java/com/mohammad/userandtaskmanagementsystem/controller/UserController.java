package com.mohammad.userandtaskmanagementsystem.controller;

import com.mohammad.userandtaskmanagementsystem.dto.ProfileUpdateRequest;
import com.mohammad.userandtaskmanagementsystem.dto.RoleUpdateRequest;
import com.mohammad.userandtaskmanagementsystem.dto.StatusUpdateRequest;
import com.mohammad.userandtaskmanagementsystem.dto.UserRequest;
import com.mohammad.userandtaskmanagementsystem.dto.UserResponse;
import com.mohammad.userandtaskmanagementsystem.entity.UserStatus;
import com.mohammad.userandtaskmanagementsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ==========================================
    // MY PROFILE
    // ADMIN + USER
    // ==========================================

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(
            Authentication authentication) {

        UserResponse response =
                userService.getMyProfile(
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // UPDATE MY PROFILE
    // ADMIN + USER
    // ==========================================

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMyProfile(
            @Valid @RequestBody ProfileUpdateRequest request,
            Authentication authentication) {

        UserResponse response =
                userService.updateMyProfile(
                        authentication.getName(),
                        request
                );

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // SEARCH / FILTER USERS
    // ADMIN ONLY
    // ==========================================

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false) UserStatus status) {

        return ResponseEntity.ok(
                userService.searchUsers(
                        search,
                        roleId,
                        status
                )
        );
    }

    // ==========================================
    // GET ALL USERS
    // ADMIN ONLY
    // ==========================================

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }

    // ==========================================
    // GET USER BY ID
    // ADMIN ONLY
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                userService.getUserById(id)
        );
    }

    // ==========================================
    // CREATE USER
    // ADMIN ONLY
    // ==========================================

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserRequest request) {

        UserResponse response =
                userService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ==========================================
    // UPDATE USER
    // ADMIN ONLY
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {

        return ResponseEntity.ok(
                userService.updateUser(
                        id,
                        request
                )
        );
    }

    // ==========================================
    // DELETE USER
    // ADMIN ONLY
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    // ==========================================
    // UPDATE USER ROLE
    // ADMIN ONLY
    // ==========================================

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequest request) {

        UserResponse response =
                userService.updateUserRole(
                        id,
                        request.getRoleId()
                );

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // UPDATE USER STATUS
    // ADMIN ONLY
    // ==========================================

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {

        UserResponse response =
                userService.updateUserStatus(
                        id,
                        request.getStatus()
                );

        return ResponseEntity.ok(response);
    }
}