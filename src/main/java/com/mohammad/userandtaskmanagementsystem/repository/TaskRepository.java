package com.mohammad.userandtaskmanagementsystem.repository;

import com.mohammad.userandtaskmanagementsystem.entity.Task;
import com.mohammad.userandtaskmanagementsystem.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository
        extends JpaRepository<Task, Long> {

    List<Task> findByDeletedFalse();

    Optional<Task> findByIdAndDeletedFalse(Long id);

    List<Task> findByAssignedUserIdAndDeletedFalse(
            Long userId
    );

    List<Task> findByStatusAndDeletedFalse(
            TaskStatus status
    );
}