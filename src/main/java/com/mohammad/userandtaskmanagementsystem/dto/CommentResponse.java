package com.mohammad.userandtaskmanagementsystem.dto;

import java.time.LocalDateTime;

public class CommentResponse {

    private Long id;
    private Long taskId;
    private Long userId;
    private String userName;
    private Long parentCommentId;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentResponse() {
    }

    public CommentResponse(Long id,
                           Long taskId,
                           Long userId,
                           String userName,
                           Long parentCommentId,
                           String comment,
                           LocalDateTime createdAt,
                           LocalDateTime updatedAt) {
        this.id = id;
        this.taskId = taskId;
        this.userId = userId;
        this.userName = userName;
        this.parentCommentId = parentCommentId;
        this.comment = comment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}