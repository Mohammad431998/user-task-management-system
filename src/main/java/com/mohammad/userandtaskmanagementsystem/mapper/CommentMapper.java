package com.mohammad.userandtaskmanagementsystem.mapper;

import com.mohammad.userandtaskmanagementsystem.dto.CommentResponse;
import com.mohammad.userandtaskmanagementsystem.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentResponse toResponse(Comment comment) {

        Long parentCommentId = comment.getParentComment() != null
                ? comment.getParentComment().getId()
                : null;

        return new CommentResponse(
                comment.getId(),
                comment.getTask().getId(),
                comment.getUser().getId(),
                comment.getUser().getName(),
                parentCommentId,
                comment.getComment(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}