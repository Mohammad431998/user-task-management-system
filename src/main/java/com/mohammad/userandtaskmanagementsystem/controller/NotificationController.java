package com.mohammad.userandtaskmanagementsystem.controller;

import com.mohammad.userandtaskmanagementsystem.dto.NotificationRequest;
import com.mohammad.userandtaskmanagementsystem.dto.NotificationResponse;
import com.mohammad.userandtaskmanagementsystem.entity.Notification;
import com.mohammad.userandtaskmanagementsystem.mapper.NotificationMapper;
import com.mohammad.userandtaskmanagementsystem.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    public NotificationController(NotificationService notificationService,
                                  NotificationMapper notificationMapper) {
        this.notificationService = notificationService;
        this.notificationMapper = notificationMapper;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(
            @PathVariable Long userId) {

        List<NotificationResponse> notifications = notificationService
                .getUserNotifications(userId)
                .stream()
                .map(notificationMapper::toResponse)
                .toList();

        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(
            @PathVariable Long userId) {

        List<NotificationResponse> notifications = notificationService
                .getUnreadNotifications(userId)
                .stream()
                .map(notificationMapper::toResponse)
                .toList();

        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotificationById(
            @PathVariable Long id) {

        Notification notification =
                notificationService.getNotificationById(id);

        return ResponseEntity.ok(
                notificationMapper.toResponse(notification)
        );
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(
            @Valid @RequestBody NotificationRequest request) {

        Notification notification = notificationService.createNotification(
                request.getUserId(),
                request.getTaskId(),
                request.getTitle(),
                request.getMessage(),
                request.getType()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(notificationMapper.toResponse(notification));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long id) {

        Notification notification =
                notificationService.markAsRead(id);

        return ResponseEntity.ok(
                notificationMapper.toResponse(notification)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long id) {

        notificationService.deleteNotification(id);

        return ResponseEntity.noContent().build();
    }
    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<Long> getUnreadCount(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                notificationService.getUnreadCount(userId)
        );
    }
}