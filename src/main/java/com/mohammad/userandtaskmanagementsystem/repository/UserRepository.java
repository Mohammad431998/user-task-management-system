package com.mohammad.userandtaskmanagementsystem.repository;

import com.mohammad.userandtaskmanagementsystem.entity.User;
import com.mohammad.userandtaskmanagementsystem.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // =========================
    // GET ACTIVE USERS
    // =========================

    List<User> findByDeletedFalse();

    // =========================
    // GET ACTIVE USER BY ID
    // =========================

    Optional<User> findByIdAndDeletedFalse(Long id);

    // =========================
    // GET ACTIVE USER BY EMAIL
    // =========================

    @Query("""
        SELECT u
        FROM User u
        JOIN FETCH u.role
        WHERE u.email = :email
          AND u.deleted = false
        """)
    Optional<User> findByEmailAndDeletedFalse(
            @Param("email") String email
    );

    // =========================
    // GET ACTIVE USER BY USERNAME
    // =========================

    @Query("""
        SELECT u
        FROM User u
        JOIN FETCH u.role
        WHERE u.username = :username
          AND u.deleted = false
        """)
    Optional<User> findByUsernameAndDeletedFalse(
            @Param("username") String username
    );

    // =========================
    // CHECK EMAIL
    // =========================

    boolean existsByEmailAndDeletedFalse(String email);

    // =========================
    // CHECK USERNAME
    // =========================

    boolean existsByUsernameAndDeletedFalse(String username);

    // =========================
    // SEARCH / FILTER ACTIVE USERS
    // =========================

    @Query("""
        SELECT u
        FROM User u
        JOIN FETCH u.role
        WHERE u.deleted = false
          AND (
                :search IS NULL OR
                LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR
                LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
              )
          AND (:roleId IS NULL OR u.role.id = :roleId)
          AND (:status IS NULL OR u.status = :status)
        """)
    List<User> searchUsers(
            @Param("search") String search,
            @Param("roleId") Long roleId,
            @Param("status") UserStatus status
    );
}