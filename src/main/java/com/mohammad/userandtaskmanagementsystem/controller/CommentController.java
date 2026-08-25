package com.mohammad.userandtaskmanagementsystem.controller;

import com.mohammad.userandtaskmanagementsystem.dto.CommentRequest;
import com.mohammad.userandtaskmanagementsystem.dto.CommentResponse;
import com.mohammad.userandtaskmanagementsystem.entity.Comment;
import com.mohammad.userandtaskmanagementsystem.entity.User;
import com.mohammad.userandtaskmanagementsystem.mapper.CommentMapper;
import com.mohammad.userandtaskmanagementsystem.service.CommentService;
import com.mohammad.userandtaskmanagementsystem.service.TaskService;
import com.mohammad.userandtaskmanagementsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final UserService userService;
    private final TaskService taskService;

    public CommentController(
            CommentService commentService,
            CommentMapper commentMapper,
            UserService userService,
            TaskService taskService) {

        this.commentService = commentService;
        this.commentMapper = commentMapper;
        this.userService = userService;
        this.taskService = taskService;
    }

    // ==========================================
    // VIEW COMMENTS OF TASK
    // ADMIN + ASSIGNED USER ONLY
    // ==========================================

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByTask(
            @PathVariable Long taskId,
            Authentication authentication) {

        User user = userService.getUserByUsername(
                authentication.getName()
        );

        boolean isAdmin = user.getRole()
                .getName()
                .equalsIgnoreCase("ADMIN");

        // Ensures the task exists and, for non-admins,
        // that it is actually assigned to them before
        // exposing its comments.
        if (isAdmin) {
            taskService.getTaskById(taskId);
        } else {
            taskService.getTaskForUser(taskId, user.getId());
        }

        List<CommentResponse> comments = commentService
                .getCommentsByTask(taskId)
                .stream()
                .map(commentMapper::toResponse)
                .toList();

        return ResponseEntity.ok(comments);
    }

    // ==========================================
    // VIEW COMMENT
    // ADMIN + USER
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponse> getCommentById(
            @PathVariable Long id) {

        Comment comment =
                commentService.getCommentById(id);

        return ResponseEntity.ok(
                commentMapper.toResponse(comment)
        );
    }

    // ==========================================
    // VIEW REPLIES
    // ADMIN + USER
    // ==========================================

    @GetMapping("/{id}/replies")
    public ResponseEntity<List<CommentResponse>> getReplies(
            @PathVariable Long id) {

        List<CommentResponse> replies = commentService
                .getReplies(id)
                .stream()
                .map(commentMapper::toResponse)
                .toList();

        return ResponseEntity.ok(replies);
    }

    // ==========================================
    // ADD COMMENT
    // ADMIN + USER
    // ==========================================

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {

        User user = userService.getUserByEmail(
                authentication.getName()
        );

        Comment comment =
                commentService.createComment(
                        request,
                        user.getId()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(commentMapper.toResponse(comment));
    }

    // ==========================================
    // DELETE COMMENT
    // ADMIN ONLY
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id) {

        commentService.deleteComment(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}