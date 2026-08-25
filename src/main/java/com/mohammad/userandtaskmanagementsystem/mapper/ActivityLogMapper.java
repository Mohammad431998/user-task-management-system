package com.mohammad.userandtaskmanagementsystem.mapper;

import com.mohammad.userandtaskmanagementsystem.dto.ActivityLogResponse;
import com.mohammad.userandtaskmanagementsystem.entity.ActivityLog;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogMapper {

    public ActivityLogResponse toResponse(ActivityLog log) {

        Long userId = log.getUser() != null
                ? log.getUser().getId()
                : null;

        return new ActivityLogResponse(
                log.getId(),
                userId,
                log.getAction(),
                log.getEntityType(),
                log.getEntityId(),
                log.getDescription(),
                log.getCreatedAt()
        );
    }
}