package com.mohammad.userandtaskmanagementsystem.controller;

import com.mohammad.userandtaskmanagementsystem.dto.RoleResponse;
import com.mohammad.userandtaskmanagementsystem.repository.RoleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Exposes the available roles (ADMIN / USER) so the frontend
 * can populate role selectors when creating/editing users.
 */
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleRepository roleRepository;

    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles() {

        List<RoleResponse> roles = roleRepository.findAll()
                .stream()
                .map(role -> new RoleResponse(role.getId(), role.getName()))
                .toList();

        return ResponseEntity.ok(roles);
    }
}
