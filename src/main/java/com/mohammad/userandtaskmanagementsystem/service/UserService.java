package com.mohammad.userandtaskmanagementsystem.service;

import com.mohammad.userandtaskmanagementsystem.dto.ProfileUpdateRequest;
import com.mohammad.userandtaskmanagementsystem.dto.UserRequest;
import com.mohammad.userandtaskmanagementsystem.dto.UserResponse;
import com.mohammad.userandtaskmanagementsystem.entity.Role;
import com.mohammad.userandtaskmanagementsystem.entity.User;
import com.mohammad.userandtaskmanagementsystem.entity.UserStatus;
import com.mohammad.userandtaskmanagementsystem.mapper.UserMapper;
import com.mohammad.userandtaskmanagementsystem.repository.RoleRepository;
import com.mohammad.userandtaskmanagementsystem.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            ActivityLogService activityLogService,
            NotificationService notificationService) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.activityLogService = activityLogService;
        this.notificationService = notificationService;
    }

    // =========================
    // GET ALL ACTIVE USERS
    // ADMIN
    // =========================

    public List<UserResponse> getAllUsers() {

        return userRepository
                .findByDeletedFalse()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    // =========================
    // GET USER BY ID
    // ADMIN
    // =========================

    public UserResponse getUserById(Long id) {

        User user = findUserById(id);

        return userMapper.toResponse(user);
    }

    // =========================
    // GET USER ENTITY BY ID
    // =========================

    public User getUserEntityById(Long id) {

        return findUserById(id);
    }

    // =========================
    // GET USER BY EMAIL
    // =========================

    public User getUserByEmail(String email) {

        return userRepository
                .findByEmailAndDeletedFalse(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    // =========================
    // GET USER BY USERNAME
    // AUTHENTICATION
    // =========================

    public User getUserByUsername(String username) {

        return userRepository
                .findByUsernameAndDeletedFalse(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    // =========================
    // CREATE USER
    // ADMIN
    // =========================

    public UserResponse createUser(UserRequest request) {

        if (userRepository.existsByEmailAndDeletedFalse(
                request.getEmail())) {

            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByUsernameAndDeletedFalse(
                request.getUsername())) {

            throw new RuntimeException("Username already exists");
        }

        if (request.getPassword() == null
                || request.getPassword().isBlank()) {

            throw new RuntimeException("Password is required");
        }

        if (request.getPassword().length() < 8) {

            throw new RuntimeException(
                    "Password must be at least 8 characters"
            );
        }

        Role role = getRoleById(request.getRoleId());

        User user = new User();

        user.setUsername(request.getUsername());
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setRole(role);

        User savedUser = userRepository.save(user);

        // Activity Log
        activityLogService.createLog(
                null,
                "CREATE",
                "USER",
                savedUser.getId(),
                "User created: " + savedUser.getName()
        );

        // Notification
        notificationService.createNotification(
                savedUser.getId(),
                null,
                "User Account Created",
                "Your user account has been created by an administrator.",
                "USER_CREATED"
        );

        return userMapper.toResponse(savedUser);
    }

    // =========================
    // UPDATE USER
    // ADMIN
    // =========================

    public UserResponse updateUser(
            Long id,
            UserRequest request) {

        User existingUser = findUserById(id);

        // Check email duplication
        if (!existingUser.getEmail().equals(request.getEmail())
                && userRepository.existsByEmailAndDeletedFalse(
                request.getEmail())) {

            throw new RuntimeException("Email already exists");
        }

        // Check username duplication
        if (!existingUser.getUsername().equals(request.getUsername())
                && userRepository.existsByUsernameAndDeletedFalse(
                request.getUsername())) {

            throw new RuntimeException("Username already exists");
        }

        Role role = getRoleById(request.getRoleId());

        existingUser.setUsername(request.getUsername());
        existingUser.setName(request.getName());
        existingUser.setEmail(request.getEmail());
        existingUser.setRole(role);

        // Password update (optional on edit)
        if (request.getPassword() != null
                && !request.getPassword().isBlank()) {

            if (request.getPassword().length() < 8) {

                throw new RuntimeException(
                        "Password must be at least 8 characters"
                );
            }

            existingUser.setPassword(
                    passwordEncoder.encode(
                            request.getPassword()
                    )
            );
        }

        User updatedUser =
                userRepository.save(existingUser);

        // Activity Log
        activityLogService.createLog(
                null,
                "UPDATE",
                "USER",
                updatedUser.getId(),
                "User updated: " + updatedUser.getName()
        );

        // Notification
        notificationService.createNotification(
                updatedUser.getId(),
                null,
                "User Account Updated",
                "Your user account has been updated by an administrator.",
                "USER_UPDATED"
        );

        return userMapper.toResponse(updatedUser);
    }

    // =========================
    // SOFT DELETE USER
    // ADMIN
    // =========================

    public void deleteUser(Long id) {

        User user = findUserById(id);

        // Activity Log
        activityLogService.createLog(
                null,
                "DELETE",
                "USER",
                user.getId(),
                "User deleted: " + user.getName()
        );

        // Soft Delete
        user.setDeleted(true);

        userRepository.save(user);
    }

    // =========================
    // FIND ACTIVE USER
    // =========================

    private User findUserById(Long id) {

        return userRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    // =========================
    // GET ROLE
    // =========================

    private Role getRoleById(Long roleId) {

        return roleRepository
                .findById(roleId)
                .orElseThrow(() ->
                        new RuntimeException("Role not found"));
    }

    // =========================
    // UPDATE USER ROLE
    // ADMIN
    // =========================

    public UserResponse updateUserRole(
            Long id,
            Long roleId) {

        User user = findUserById(id);

        Role role = getRoleById(roleId);

        user.setRole(role);

        User updatedUser =
                userRepository.save(user);

        // Activity Log
        activityLogService.createLog(
                null,
                "UPDATE_ROLE",
                "USER",
                updatedUser.getId(),
                "User role updated: "
                        + updatedUser.getName()
        );

        // Notification
        notificationService.createNotification(
                updatedUser.getId(),
                null,
                "Role Updated",
                "Your user role has been updated by an administrator.",
                "USER_ROLE_UPDATED"
        );

        return userMapper.toResponse(updatedUser);
    }

    // =========================
    // UPDATE USER STATUS
    // ADMIN
    // =========================

    public UserResponse updateUserStatus(
            Long id,
            UserStatus status) {

        User user = findUserById(id);

        user.setStatus(status);

        User updatedUser =
                userRepository.save(user);

        // Activity Log
        activityLogService.createLog(
                null,
                "UPDATE_STATUS",
                "USER",
                updatedUser.getId(),
                "User status updated: "
                        + updatedUser.getName()
        );

        // Notification
        notificationService.createNotification(
                updatedUser.getId(),
                null,
                "Status Updated",
                "Your account status has been updated by an administrator.",
                "USER_STATUS_UPDATED"
        );

        return userMapper.toResponse(updatedUser);
    }

    // =========================
    // SEARCH / FILTER USERS
    // ADMIN
    // =========================

    public List<UserResponse> searchUsers(
            String search,
            Long roleId,
            UserStatus status) {

        return userRepository
                .searchUsers(
                        search,
                        roleId,
                        status
                )
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    // =========================
    // GET MY PROFILE
    // ADMIN + USER
    // =========================

    public UserResponse getMyProfile(
            String username) {

        User user = getUserByUsername(username);

        return userMapper.toResponse(user);
    }

    // =========================
    // UPDATE MY PROFILE
    // ADMIN + USER
    // =========================

    public UserResponse updateMyProfile(
            String currentUsername,
            ProfileUpdateRequest request) {

        User user =
                getUserByUsername(currentUsername);

        // Check email duplication
        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmailAndDeletedFalse(
                request.getEmail())) {

            throw new RuntimeException(
                    "Email already exists"
            );
        }

        // Update allowed profile fields
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        if (request.getPassword() != null
                && !request.getPassword().isBlank()) {

            user.setPassword(
                    passwordEncoder.encode(
                            request.getPassword()
                    )
            );
        }

        User updatedUser =
                userRepository.save(user);

        // Activity Log
        activityLogService.createLog(
                updatedUser.getId(),
                "UPDATE_PROFILE",
                "USER",
                updatedUser.getId(),
                "User updated own profile: "
                        + updatedUser.getName()
        );

        return userMapper.toResponse(updatedUser);
    }
}