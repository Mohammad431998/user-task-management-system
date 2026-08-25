package com.mohammad.userandtaskmanagementsystem.mapper;

import com.mohammad.userandtaskmanagementsystem.dto.TaskResponse;
import com.mohammad.userandtaskmanagementsystem.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getAssignedUser().getId(),
                task.getAssignedUser().getName(),
                task.getStatus(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}