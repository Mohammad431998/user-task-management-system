package com.mohammad.userandtaskmanagementsystem.service;

import com.mohammad.userandtaskmanagementsystem.dto.ActivityLogResponse;
import com.mohammad.userandtaskmanagementsystem.entity.ActivityLog;
import com.mohammad.userandtaskmanagementsystem.mapper.ActivityLogMapper;
import com.mohammad.userandtaskmanagementsystem.repository.ActivityLogRepository;
import com.mohammad.userandtaskmanagementsystem.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ActivityLogMapper activityLogMapper;

    public ActivityLogService(
            ActivityLogRepository activityLogRepository,
            UserRepository userRepository,
            SimpMessagingTemplate messagingTemplate,
            ActivityLogMapper activityLogMapper) {

        this.activityLogRepository = activityLogRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
        this.activityLogMapper = activityLogMapper;
    }

    public List<ActivityLog> getAllLogs() {
        return activityLogRepository.findAll();
    }

    public List<ActivityLog> getLogsByUser(Long userId) {
        return activityLogRepository
                .findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<ActivityLog> getLogsByEntity(
            String entityType,
            Long entityId) {

        return activityLogRepository
                .findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
                        entityType,
                        entityId
                );
    }

    public ActivityLog createLog(
            Long userId,
            String action,
            String entityType,
            Long entityId,
            String description) {

        ActivityLog log = new ActivityLog();

        if (userId != null) {
            log.setUser(
                    userRepository.findById(userId)
                            .orElseThrow(() ->
                                    new RuntimeException("User not found"))
            );
        }

        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDescription(description);

        ActivityLog savedLog =
                activityLogRepository.save(log);

        // =========================
        // REAL-TIME PUSH
        // =========================
        // Stream every action to the admin
        // activity feed as it happens.
        ActivityLogResponse payload =
                activityLogMapper.toResponse(savedLog);

        messagingTemplate.convertAndSend(
                "/topic/activity",
                payload
        );

        return savedLog;
    }
}