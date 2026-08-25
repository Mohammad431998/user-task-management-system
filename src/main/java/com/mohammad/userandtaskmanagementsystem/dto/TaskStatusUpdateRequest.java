package com.mohammad.userandtaskmanagementsystem.dto;

import com.mohammad.userandtaskmanagementsystem.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;

public class TaskStatusUpdateRequest {

    @NotNull
    private TaskStatus status;

    public TaskStatusUpdateRequest() {
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}