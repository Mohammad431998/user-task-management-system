package com.mohammad.userandtaskmanagementsystem.dto;

import jakarta.validation.constraints.NotNull;

public class RoleUpdateRequest {

    @NotNull
    private Long roleId;

    public RoleUpdateRequest() {
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}