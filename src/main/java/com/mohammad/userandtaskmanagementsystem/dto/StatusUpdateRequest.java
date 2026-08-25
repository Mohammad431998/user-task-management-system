package com.mohammad.userandtaskmanagementsystem.dto;

import com.mohammad.userandtaskmanagementsystem.entity.UserStatus;
import jakarta.validation.constraints.NotNull;

public class StatusUpdateRequest {

    @NotNull
    private UserStatus status;

    public StatusUpdateRequest() {
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}