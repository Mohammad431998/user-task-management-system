package com.mohammad.userandtaskmanagementsystem.repository;

import com.mohammad.userandtaskmanagementsystem.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Active comments for a task
    List<Comment> findByTaskIdAndDeletedFalse(Long taskId);

    // Active comment by ID
    Optional<Comment> findByIdAndDeletedFalse(Long id);

    // Active replies
    List<Comment> findByParentCommentIdAndDeletedFalse(
            Long parentCommentId
    );
}