package com.mohammad.userandtaskmanagementsystem.controller;

import com.mohammad.userandtaskmanagementsystem.dto.AssignTaskRequest;
import com.mohammad.userandtaskmanagementsystem.dto.TaskRequest;
import com.mohammad.userandtaskmanagementsystem.dto.TaskResponse;
import com.mohammad.userandtaskmanagementsystem.dto.TaskStatusUpdateRequest;
import com.mohammad.userandtaskmanagementsystem.entity.Task;
import com.mohammad.userandtaskmanagementsystem.entity.User;
import com.mohammad.userandtaskmanagementsystem.mapper.TaskMapper;
import com.mohammad.userandtaskmanagementsystem.service.TaskService;
import com.mohammad.userandtaskmanagementsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;
    private final UserService userService;

    public TaskController(
            TaskService taskService,
            TaskMapper taskMapper,
            UserService userService) {

        this.taskService = taskService;
        this.taskMapper = taskMapper;
        this.userService = userService;
    }

    // ==========================================
    // ADMIN - VIEW ALL TASKS
    // ==========================================

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {

        List<TaskResponse> tasks = taskService
                .getAllTasks()
                .stream()
                .map(taskMapper::toResponse)
                .toList();

        return ResponseEntity.ok(tasks);
    }

    // ==========================================
    // VIEW TASK DETAILS
    // ADMIN + ASSIGNED USER
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(
            @PathVariable Long id,
            Authentication authentication) {

        User user = userService.getUserByUsername(
                authentication.getName()
        );

        Task task;

        if (user.getRole().getName().equalsIgnoreCase("ADMIN")) {

            task = taskService.getTaskById(id);

        } else {

            task = taskService.getTaskForUser(
                    id,
                    user.getId()
            );
        }

        return ResponseEntity.ok(
                taskMapper.toResponse(task)
        );
    }

    // ==========================================
    // USER - VIEW ASSIGNED TASKS
    // ==========================================

    @GetMapping("/my")
    public ResponseEntity<List<TaskResponse>> getMyTasks(
            Authentication authentication) {

        User user = userService.getUserByUsername(
                authentication.getName()
        );

        List<TaskResponse> tasks = taskService
                .getTasksByUser(user.getId())
                .stream()
                .map(taskMapper::toResponse)
                .toList();

        return ResponseEntity.ok(tasks);
    }

    // ==========================================
    // ADMIN - CREATE TASK
    // ==========================================

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request) {

        Task task = taskService.createTask(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskMapper.toResponse(task));
    }

    // ==========================================
    // ADMIN - UPDATE TASK
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {

        Task task = taskService.updateTask(
                id,
                request
        );

        return ResponseEntity.ok(
                taskMapper.toResponse(task)
        );
    }

    // ==========================================
    // ADMIN - DELETE TASK
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id) {

        taskService.deleteTask(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    // ==========================================
    // ADMIN - ASSIGN TASK
    // ==========================================

    @PatchMapping("/{id}/assign")
    public ResponseEntity<TaskResponse> assignTask(
            @PathVariable Long id,
            @Valid @RequestBody AssignTaskRequest request) {

        Task task = taskService.assignTask(
                id,
                request.getUserId()
        );

        return ResponseEntity.ok(
                taskMapper.toResponse(task)
        );
    }

    // ==========================================
    // USER - UPDATE OWN TASK STATUS
    // ==========================================

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable Long id,
            @Valid @RequestBody TaskStatusUpdateRequest request,
            Authentication authentication) {

        User user = userService.getUserByUsername(
                authentication.getName()
        );

        Task task = taskService.updateTaskStatus(
                id,
                user.getId(),
                request.getStatus()
        );

        return ResponseEntity.ok(
                taskMapper.toResponse(task)
        );
    }
}