package com.mohammad.userandtaskmanagementsystem.service;

import com.mohammad.userandtaskmanagementsystem.dto.NotificationResponse;
import com.mohammad.userandtaskmanagementsystem.entity.Notification;
import com.mohammad.userandtaskmanagementsystem.mapper.NotificationMapper;
import com.mohammad.userandtaskmanagementsystem.repository.NotificationRepository;
import com.mohammad.userandtaskmanagementsystem.repository.TaskRepository;
import com.mohammad.userandtaskmanagementsystem.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationMapper notificationMapper;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            TaskRepository taskRepository,
            SimpMessagingTemplate messagingTemplate,
            NotificationMapper notificationMapper) {

        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.messagingTemplate = messagingTemplate;
        this.notificationMapper = notificationMapper;
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository
                .findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Notification not found"));
    }

    public Notification createNotification(
            Long userId,
            Long taskId,
            String title,
            String message,
            String type) {

        Notification notification = new Notification();

        notification.setUser(
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException("User not found"))
        );

        if (taskId != null) {
            notification.setTask(
                    taskRepository.findById(taskId)
                            .orElseThrow(() ->
                                    new RuntimeException("Task not found"))
            );
        }

        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);

        Notification savedNotification =
                notificationRepository.save(notification);

        // =========================
        // REAL-TIME PUSH
        // =========================
        // Deliver instantly to the target user's
        // personal notification channel.
        NotificationResponse payload =
                notificationMapper.toResponse(savedNotification);

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + userId,
                payload
        );

        return savedNotification;
    }

    public Notification markAsRead(Long id) {

        Notification notification =
                getNotificationById(id);

        notification.setRead(true);

        return notificationRepository.save(notification);
    }

    public void deleteNotification(Long id) {

        Notification notification =
                getNotificationById(id);

        notificationRepository.delete(notification);
    }
    public long getUnreadCount(Long userId) {
        return notificationRepository
                .countByUserIdAndReadFalse(userId);
    }
}