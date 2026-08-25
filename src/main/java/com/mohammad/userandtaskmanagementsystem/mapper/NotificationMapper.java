package com.mohammad.userandtaskmanagementsystem.mapper;

import com.mohammad.userandtaskmanagementsystem.dto.NotificationResponse;
import com.mohammad.userandtaskmanagementsystem.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {

        Long taskId = notification.getTask() != null
                ? notification.getTask().getId()
                : null;

        return new NotificationResponse(
                notification.getId(),
                notification.getUser().getId(),
                taskId,
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}