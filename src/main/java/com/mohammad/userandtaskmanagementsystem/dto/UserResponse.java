package com.mohammad.userandtaskmanagementsystem.dto;

import com.mohammad.userandtaskmanagementsystem.entity.UserStatus;

public class UserResponse {

    private Long id;
    private String username;
    private String name;
    private String email;
    private Long roleId;
    private String roleName;
    private UserStatus status;

    public UserResponse() {
    }

    public UserResponse(Long id,
                        String username,
                        String name,
                        String email,
                        Long roleId,
                        String roleName,
                        UserStatus status) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.roleId = roleId;
        this.roleName = roleName;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Long getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public UserStatus getStatus() {
        return status;
    }
}