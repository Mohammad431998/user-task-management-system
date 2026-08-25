package com.mohammad.userandtaskmanagementsystem.service;

import com.mohammad.userandtaskmanagementsystem.dto.TaskRequest;
import com.mohammad.userandtaskmanagementsystem.entity.Task;
import com.mohammad.userandtaskmanagementsystem.entity.TaskStatus;
import com.mohammad.userandtaskmanagementsystem.entity.User;
import com.mohammad.userandtaskmanagementsystem.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;

    public TaskService(
            TaskRepository taskRepository,
            UserService userService,
            ActivityLogService activityLogService,
            NotificationService notificationService) {

        this.taskRepository = taskRepository;
        this.userService = userService;
        this.activityLogService = activityLogService;
        this.notificationService = notificationService;
    }

    // =========================
    // GET ALL ACTIVE TASKS
    // ADMIN
    // =========================
    public List<Task> getAllTasks() {

        return taskRepository.findByDeletedFalse();
    }

    // =========================
    // GET TASK BY ID
    // ADMIN
    // =========================
    public Task getTaskById(Long id) {

        return taskRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new RuntimeException("Task not found"));
    }

    // =========================
    // GET TASK FOR USER
    // USER
    // =========================
    public Task getTaskForUser(
            Long taskId,
            Long userId) {

        Task task = getTaskById(taskId);

        if (task.getAssignedUser() == null ||
                !task.getAssignedUser()
                        .getId()
                        .equals(userId)) {

            throw new RuntimeException(
                    "You can only access your assigned tasks"
            );
        }

        return task;
    }

    // =========================
    // GET USER'S ACTIVE TASKS
    // USER
    // =========================
    public List<Task> getTasksByUser(Long userId) {

        return taskRepository
                .findByAssignedUserIdAndDeletedFalse(userId);
    }

    // =========================
    // GET TASKS BY STATUS
    // ADMIN
    // =========================
    public List<Task> getTasksByStatus(
            TaskStatus status) {

        return taskRepository
                .findByStatusAndDeletedFalse(status);
    }

    // =========================
    // CREATE TASK
    // ADMIN
    // =========================
    public Task createTask(TaskRequest request) {

        Task task = new Task();

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setDueDate(request.getDueDate());

        User assignedUser =
                userService.getUserEntityById(
                        request.getAssignedUserId()
                );

        task.setAssignedUser(assignedUser);

        Task savedTask =
                taskRepository.save(task);

        // Activity Log
        activityLogService.createLog(
                null,
                "CREATE",
                "TASK",
                savedTask.getId(),
                "Task created: "
                        + savedTask.getTitle()
        );

        // Notification
        notificationService.createNotification(
                assignedUser.getId(),
                savedTask.getId(),
                "New Task Assigned",
                "You have been assigned a new task: "
                        + savedTask.getTitle(),
                "TASK_ASSIGNED"
        );

        return savedTask;
    }

    // =========================
    // UPDATE TASK
    // ADMIN
    // =========================
    public Task updateTask(
            Long id,
            TaskRequest request) {

        Task existingTask =
                getTaskById(id);

        TaskStatus oldStatus =
                existingTask.getStatus();

        Long oldAssignedUserId =
                existingTask.getAssignedUser() != null
                        ? existingTask
                        .getAssignedUser()
                        .getId()
                        : null;

        existingTask.setTitle(
                request.getTitle()
        );

        existingTask.setDescription(
                request.getDescription()
        );

        existingTask.setStatus(
                request.getStatus()
        );

        existingTask.setDueDate(
                request.getDueDate()
        );

        User newAssignedUser =
                userService.getUserEntityById(
                        request.getAssignedUserId()
                );

        existingTask.setAssignedUser(
                newAssignedUser
        );

        Task updatedTask =
                taskRepository.save(existingTask);

        // Activity Log
        activityLogService.createLog(
                null,
                "UPDATE",
                "TASK",
                updatedTask.getId(),
                "Task updated: "
                        + updatedTask.getTitle()
        );

        // =========================
        // STATUS CHANGE NOTIFICATION
        // =========================

        if (oldStatus != updatedTask.getStatus()) {

            notificationService.createNotification(
                    updatedTask
                            .getAssignedUser()
                            .getId(),

                    updatedTask.getId(),

                    "Task Status Changed",

                    "The status of task '"
                            + updatedTask.getTitle()
                            + "' changed from "
                            + oldStatus
                            + " to "
                            + updatedTask.getStatus(),

                    "TASK_STATUS_CHANGED"
            );
        }

        // =========================
        // ASSIGNMENT CHANGE NOTIFICATION
        // =========================

        Long newAssignedUserId =
                updatedTask
                        .getAssignedUser()
                        .getId();

        if (oldAssignedUserId == null ||
                !oldAssignedUserId.equals(
                        newAssignedUserId)) {

            notificationService.createNotification(
                    newAssignedUserId,
                    updatedTask.getId(),
                    "New Task Assigned",
                    "You have been assigned a new task: "
                            + updatedTask.getTitle(),
                    "TASK_ASSIGNED"
            );
        }

        return updatedTask;
    }

    // =========================
    // SOFT DELETE TASK
    // ADMIN
    // =========================
    public void deleteTask(Long id) {

        Task task = getTaskById(id);

        // Activity Log
        activityLogService.createLog(
                null,
                "DELETE",
                "TASK",
                task.getId(),
                "Task deleted: "
                        + task.getTitle()
        );

        // Soft Delete
        task.setDeleted(true);

        taskRepository.save(task);
    }

    // =========================
    // ASSIGN TASK
    // ADMIN
    // =========================
    public Task assignTask(
            Long taskId,
            Long userId) {

        Task task =
                getTaskById(taskId);

        Long oldAssignedUserId =
                task.getAssignedUser() != null
                        ? task
                        .getAssignedUser()
                        .getId()
                        : null;

        User newAssignedUser =
                userService.getUserEntityById(
                        userId
                );

        task.setAssignedUser(
                newAssignedUser
        );

        Task updatedTask =
                taskRepository.save(task);

        // Activity Log
        activityLogService.createLog(
                null,
                "ASSIGN",
                "TASK",
                updatedTask.getId(),
                "Task assigned to user: "
                        + newAssignedUser.getName()
        );

        // Notification only if assignment changed
        if (oldAssignedUserId == null ||
                !oldAssignedUserId.equals(userId)) {

            notificationService.createNotification(
                    userId,
                    updatedTask.getId(),
                    "New Task Assigned",
                    "You have been assigned a new task: "
                            + updatedTask.getTitle(),
                    "TASK_ASSIGNED"
            );
        }

        return updatedTask;
    }

    // =========================
    // USER UPDATE TASK STATUS
    // =========================
    public Task updateTaskStatus(
            Long taskId,
            Long userId,
            TaskStatus newStatus) {

        // This also ensures the task exists
        // and is not soft deleted.
        Task task =
                getTaskForUser(
                        taskId,
                        userId
                );

        TaskStatus oldStatus =
                task.getStatus();

        task.setStatus(newStatus);

        Task updatedTask =
                taskRepository.save(task);

        // Notification only when status changes
        if (oldStatus != newStatus) {

            notificationService.createNotification(
                    updatedTask
                            .getAssignedUser()
                            .getId(),

                    updatedTask.getId(),

                    "Task Status Changed",

                    "The status of task '"
                            + updatedTask.getTitle()
                            + "' changed from "
                            + oldStatus
                            + " to "
                            + newStatus,

                    "TASK_STATUS_CHANGED"
            );
        }

        return updatedTask;
    }
}