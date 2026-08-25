package com.mohammad.userandtaskmanagementsystem.dto;

import jakarta.validation.constraints.NotNull;

public class AssignTaskRequest {

    @NotNull
    private Long userId;

    public AssignTaskRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}