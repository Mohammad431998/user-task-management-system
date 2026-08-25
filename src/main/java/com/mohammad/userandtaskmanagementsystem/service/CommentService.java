package com.mohammad.userandtaskmanagementsystem.service;

import com.mohammad.userandtaskmanagementsystem.dto.CommentRequest;
import com.mohammad.userandtaskmanagementsystem.entity.Comment;
import com.mohammad.userandtaskmanagementsystem.entity.Task;
import com.mohammad.userandtaskmanagementsystem.entity.User;
import com.mohammad.userandtaskmanagementsystem.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final TaskService taskService;
    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;

    public CommentService(
            CommentRepository commentRepository,
            UserService userService,
            TaskService taskService,
            ActivityLogService activityLogService,
            NotificationService notificationService) {

        this.commentRepository = commentRepository;
        this.userService = userService;
        this.taskService = taskService;
        this.activityLogService = activityLogService;
        this.notificationService = notificationService;
    }

    // =========================
    // GET COMMENTS BY TASK
    // ADMIN + USER
    // =========================
    public List<Comment> getCommentsByTask(Long taskId) {

        return commentRepository
                .findByTaskIdAndDeletedFalse(taskId);
    }

    // =========================
    // GET COMMENT BY ID
    // ADMIN + USER
    // =========================
    public Comment getCommentById(Long id) {

        return commentRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Comment not found"
                        ));
    }

    // =========================
    // GET REPLIES
    // ADMIN + USER
    // =========================
    public List<Comment> getReplies(
            Long parentCommentId) {

        return commentRepository
                .findByParentCommentIdAndDeletedFalse(
                        parentCommentId
                );
    }

    // =========================
    // CREATE COMMENT
    // ADMIN + USER
    // =========================
    public Comment createComment(
            CommentRequest request,
            Long currentUserId) {

        // Get current authenticated user
        User currentUser =
                userService.getUserEntityById(
                        currentUserId
                );

        // Get active task
        Task task =
                taskService.getTaskById(
                        request.getTaskId()
                );

        // =========================
        // USER AUTHORIZATION
        // =========================
        // Admin can comment on any task.
        // User can comment only on
        // their assigned task.

        boolean isAdmin =
                currentUser.getRole()
                        .getName()
                        .equalsIgnoreCase("ADMIN");

        if (!isAdmin) {

            if (task.getAssignedUser() == null ||
                    !task.getAssignedUser()
                            .getId()
                            .equals(currentUserId)) {

                throw new RuntimeException(
                        "You can only comment on your assigned tasks"
                );
            }
        }

        Comment comment = new Comment();

        comment.setTask(task);

        // IMPORTANT:
        // Use authenticated user,
        // not userId from request.
        comment.setUser(currentUser);

        comment.setComment(
                request.getComment()
        );

        // =========================
        // PARENT COMMENT
        // =========================
        if (request.getParentCommentId() != null) {

            Comment parentComment =
                    getCommentById(
                            request.getParentCommentId()
                    );

            // Make sure parent comment
            // belongs to the same task.
            if (!parentComment
                    .getTask()
                    .getId()
                    .equals(task.getId())) {

                throw new RuntimeException(
                        "Parent comment does not belong to this task"
                );
            }

            comment.setParentComment(
                    parentComment
            );
        }

        Comment savedComment =
                commentRepository.save(comment);

        // =========================
        // ACTIVITY LOG
        // =========================
        activityLogService.createLog(
                null,
                "CREATE",
                "COMMENT",
                savedComment.getId(),
                "Comment created"
        );

        // =========================
        // NOTIFICATION
        // =========================
        // Notify assigned user.
        // Do not notify if the commenter
        // is already the assigned user.
        if (task.getAssignedUser() != null &&
                !task.getAssignedUser()
                        .getId()
                        .equals(currentUserId)) {

            notificationService.createNotification(
                    task.getAssignedUser().getId(),
                    task.getId(),
                    "New Comment",
                    "A new comment was added to task: "
                            + task.getTitle(),
                    "COMMENT_ADDED"
            );
        }

        return savedComment;
    }

    // =========================
    // SOFT DELETE COMMENT
    // ADMIN ONLY
    // =========================
    public void deleteComment(Long id) {

        Comment comment =
                getCommentById(id);

        // Activity Log
        activityLogService.createLog(
                null,
                "DELETE",
                "COMMENT",
                comment.getId(),
                "Comment deleted"
        );

        // Soft Delete
        comment.setDeleted(true);

        commentRepository.save(comment);
    }
}