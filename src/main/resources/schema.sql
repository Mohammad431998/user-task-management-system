-- =====================================================================
-- User and Task Management System - MySQL schema + seed data
--
-- This project uses spring.jpa.hibernate.ddl-auto=validate, so Hibernate
-- will NOT create/alter tables automatically. Run this script once
-- against your MySQL/MariaDB database before starting the backend.
--
-- Usage (see README.md for full instructions):
--   mysql -u root -p < src/main/resources/schema.sql
-- =====================================================================

CREATE DATABASE IF NOT EXISTS task_management_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE task_management_db;

-- Dedicated application user (matches application.properties).
-- Skip this block if you already created the user, or adjust the
-- credentials to match your own application.properties.
CREATE USER IF NOT EXISTS 'task_app'@'%' IDENTIFIED BY 'TaskApp@12345';
GRANT ALL PRIVILEGES ON task_management_db.* TO 'task_app'@'%';
FLUSH PRIVILEGES;

-- =====================================================================
-- TABLES
-- =====================================================================

CREATE TABLE IF NOT EXISTS roles (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS users (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(100) NOT NULL UNIQUE,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role_id    BIGINT NOT NULL,
    status     VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS tasks (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    title             VARCHAR(200) NOT NULL,
    description       TEXT,
    assigned_user_id  BIGINT NOT NULL,
    status            VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    due_date          DATE,
    is_deleted        BOOLEAN NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_tasks_user FOREIGN KEY (assigned_user_id) REFERENCES users (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS comments (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id            BIGINT NOT NULL,
    user_id            BIGINT NOT NULL,
    parent_comment_id  BIGINT NULL,
    comment            TEXT NOT NULL,
    is_deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_comments_task FOREIGN KEY (task_id) REFERENCES tasks (id),
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_comments_parent FOREIGN KEY (parent_comment_id) REFERENCES comments (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS notifications (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    task_id    BIGINT NULL,
    title      VARCHAR(255) NOT NULL,
    message    TEXT NOT NULL,
    type       VARCHAR(50) NOT NULL,
    is_read    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_notifications_task FOREIGN KEY (task_id) REFERENCES tasks (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS activity_logs (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NULL,
    action      VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id   BIGINT,
    description TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_activity_logs_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE = InnoDB;

-- =====================================================================
-- SEED DATA
-- =====================================================================

INSERT IGNORE INTO roles (id, name) VALUES (1, 'ADMIN'), (2, 'USER');

-- Default admin account: username "admin", password "Admin@123"
-- (BCrypt hash below corresponds to "Admin@123")
INSERT IGNORE INTO users (id, username, name, email, password, role_id, status)
VALUES (
    1,
    'admin',
    'System Administrator',
    'admin@example.com',
    '$2b$10$AvEIyth4TvqNi/hSLg8ejevaJd7fRvOeqWvqezEgm35q8YM.UtOp2',
    1,
    'ACTIVE'
);

-- Default demo user account: username "jdoe", password "User@123"
INSERT IGNORE INTO users (id, username, name, email, password, role_id, status)
VALUES (
    2,
    'jdoe',
    'John Doe',
    'jdoe@example.com',
    '$2b$10$NiXpTnUGiSItXOT2VTDc8.XbtAVc8JPINmpeq3gXA/QOqb4JbvHlS',
    2,
    'ACTIVE'
);
