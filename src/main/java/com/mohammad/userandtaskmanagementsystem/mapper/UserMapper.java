package com.mohammad.userandtaskmanagementsystem.mapper;

import com.mohammad.userandtaskmanagementsystem.dto.UserResponse;
import com.mohammad.userandtaskmanagementsystem.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getRole().getId(),
                user.getRole().getName(),
                user.getStatus()
        );
    }
}