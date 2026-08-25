package com.mohammad.userandtaskmanagementsystem.dto;

import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;
    private Long userId;
    private Long taskId;
    private String title;
    private String message;
    private String type;
    private boolean read;
    private LocalDateTime createdAt;

    public NotificationResponse() {
    }

    public NotificationResponse(Long id,
                                Long userId,
                                Long taskId,
                                String title,
                                String message,
                                String type,
                                boolean read,
                                LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.taskId = taskId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.read = read;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public boolean isRead() {
        return read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}