package com.mohammad.userandtaskmanagementsystem.controller;

import com.mohammad.userandtaskmanagementsystem.dto.ActivityLogResponse;
import com.mohammad.userandtaskmanagementsystem.entity.ActivityLog;
import com.mohammad.userandtaskmanagementsystem.mapper.ActivityLogMapper;
import com.mohammad.userandtaskmanagementsystem.service.ActivityLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;
    private final ActivityLogMapper activityLogMapper;

    public ActivityLogController(ActivityLogService activityLogService,
                                 ActivityLogMapper activityLogMapper) {
        this.activityLogService = activityLogService;
        this.activityLogMapper = activityLogMapper;
    }

    @GetMapping
    public ResponseEntity<List<ActivityLogResponse>> getAllLogs() {

        List<ActivityLogResponse> logs = activityLogService
                .getAllLogs()
                .stream()
                .map(activityLogMapper::toResponse)
                .toList();

        return ResponseEntity.ok(logs);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActivityLogResponse>> getLogsByUser(
            @PathVariable Long userId) {

        List<ActivityLogResponse> logs = activityLogService
                .getLogsByUser(userId)
                .stream()
                .map(activityLogMapper::toResponse)
                .toList();

        return ResponseEntity.ok(logs);
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<ActivityLogResponse>> getLogsByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {

        List<ActivityLogResponse> logs = activityLogService
                .getLogsByEntity(entityType, entityId)
                .stream()
                .map(activityLogMapper::toResponse)
                .toList();

        return ResponseEntity.ok(logs);
    }
}