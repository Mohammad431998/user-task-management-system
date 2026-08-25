# User and Task Management System

A full-stack application where **Admins** manage users and tasks, and **Users** manage the
tasks assigned to them — with role-based access control, JWT authentication, threaded task
comments, a live activity log, and real-time WebSocket notifications.

- **Backend:** Java 17, Spring Boot 4 (Web, Security, Data JPA, WebSocket), JWT (jjwt)
- **Frontend:** Angular 18 (standalone components), Bootstrap 5, Bootstrap Icons, STOMP over SockJS
- **Database:** MySQL / MariaDB (chosen for a production-like setup — see below)
🎥 Demo Video

Watch the Project Demo

https://drive.google.com/file/d/1EQC2LggbfuI2kzX30kUC20mgJkHOGgTY/view?usp=sharing

## Project Structure

```
UserAndTaskManagementSystem/       # Spring Boot backend (this folder)
├── src/main/java/...              # Controllers, services, entities, security, WebSocket config
├── src/main/resources/
│   ├── application.properties     # DB connection, JWT secret, server port
│   └── schema.sql                 # MySQL schema + seed data (roles, demo accounts)
└── frontend/                      # Angular application
    └── src/app/
        ├── core/                  # models, services, guards, interceptors
        ├── layout/                # shell (navbar + sidebar), notification bell
        └── features/              # auth, admin, user, profile, tasks (comments)
```

## 1. Database Setup (MySQL)

This project uses **MySQL** (chosen over the embedded H2 option for a more production-like
setup). `spring.jpa.hibernate.ddl-auto=validate` is used, so the schema must be created
**before** starting the backend — Hibernate will only validate it, not generate it.

1. Make sure MySQL/MariaDB is installed and running.
2. Run the provided schema script, which creates the database, an application user, all
   tables, and seed data:

   ```bash
   mysql -u root -p < src/main/resources/schema.sql
   ```

   This creates:
   - Database `task_management_db`
   - User `task_app` / password `TaskApp@12345` (matches `application.properties`)
   - Tables: `roles`, `users`, `tasks`, `comments`, `notifications`, `activity_logs`
   - Seed data:
     - Roles: `ADMIN`, `USER`
     - Demo admin account: **username `admin` / password `Admin@123`**
     - Demo user account: **username `jdoe` / password `User@123`**

3. If your MySQL runs on a different host/port, or you use different credentials, update
   `src/main/resources/application.properties` accordingly:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3307/task_management_db
   spring.datasource.username=task_app
   spring.datasource.password=TaskApp@12345
   ```

   > Prefer H2 instead? Swap the `mysql-connector-j` dependency/datasource properties for the
   > H2 in-memory driver and set `ddl-auto=update` so Hibernate creates the schema
   > automatically; the rest of the application code is database-agnostic.

## 2. Running the Backend

From the project root (this folder):

```bash
./mvnw spring-boot:run
```

- The API starts on **http://localhost:8081**
- WebSocket (STOMP/SockJS) endpoint: **http://localhost:8081/ws**
- All REST endpoints are under `/api/**` (see [API Overview](#api-overview) below)

## 3. Running the Frontend

```bash
cd frontend
npm install
npm start
```

- The app starts on **http://localhost:4200** and talks to the backend at
  `http://localhost:8081/api` (configured in `frontend/src/environments/environment.ts`).
- Log in with one of the seeded accounts:
  - **Admin:** `admin` / `Admin@123`
  - **User:** `jdoe` / `User@123`

## Features Implemented

### Authentication & Roles
- JWT-based login (`POST /api/auth/login`), token stored client-side and attached to every
  request via an HTTP interceptor.
- Route guards redirect Admins to `/admin/*` and Users to `/user/*`; unauthorized access
  (401/expired token) redirects back to the login page.
- Passwords are hashed with BCrypt before being stored.

### Admin Dashboard
- **Overview:** total users, total tasks, pending/in-progress/completed breakdown, and a
  completion-rate progress bar.
- **User Management:** list, search/filter (by name/email, role, status), create, edit,
  activate/deactivate, and delete users; roles are assigned from a live dropdown.
- **Task Management:** list, filter, create, edit, assign, and delete tasks. Task details
  page shows full task info plus a **threaded comment** section (replies are nested).
- **Activity Log:** a live-updating (WebSocket-fed) audit trail of logins, logouts, and all
  user/task/comment CRUD actions with timestamps.

### User Dashboard
- **My Tasks:** table of tasks assigned to the logged-in user with quick inline status
  updates (Pending / In Progress / Completed).
- **Task Details:** same page as Admin's task detail (adapted by role) — view full task info
  and post/reply to comments on tasks assigned to them.

### Profile (Admin & User)
- View and update name, email, and password. Email uniqueness and strong-password rules
  (min 8 chars, upper/lower case, number, symbol) are validated on both the client and server.

### Real-Time Notifications
- WebSocket (STOMP/SockJS) connection opened after login; users are pushed notifications the
  instant a task is assigned to them, their task status changes, a comment is added, or an
  admin updates their account — no polling required.
- A bell icon in the header shows the unread count; clicking a notification (or "mark all
  read") marks it as read, persisted in the database.

## API Overview

| Method | Endpoint                         | Access            | Description                    |
|--------|-----------------------------------|--------------------|--------------------------------|
| POST   | `/api/auth/login`                | Public             | Authenticate, returns JWT      |
| POST   | `/api/auth/logout`                | Authenticated       | Logs a logout activity entry   |
| GET    | `/api/users`, `/api/users/search`| Admin              | List / search users            |
| POST/PUT/DELETE | `/api/users/{id}`        | Admin              | Create / update / delete users |
| GET/PUT| `/api/users/me`                  | Authenticated       | View / update own profile      |
| GET    | `/api/roles`                     | Admin              | List roles (for dropdowns)     |
| GET    | `/api/tasks`                     | Admin              | List all tasks                 |
| GET    | `/api/tasks/my`                  | User               | List tasks assigned to me      |
| POST/PUT/DELETE | `/api/tasks/{id}`        | Admin              | Create / update / delete tasks |
| PATCH  | `/api/tasks/{id}/assign`         | Admin              | Reassign a task                |
| PATCH  | `/api/tasks/{id}/status`         | User               | Update status of my own task   |
| GET/POST | `/api/comments/**`             | Authenticated       | View / add threaded comments   |
| GET    | `/api/notifications/user/{id}`   | Authenticated       | List notifications             |
| PATCH  | `/api/notifications/{id}/read`   | Authenticated       | Mark a notification as read    |
| GET    | `/api/activity-logs`             | Admin              | Full audit trail                |

All protected endpoints require an `Authorization: Bearer <token>` header. Unauthorized or
expired requests receive a `401` with a JSON error body.

## Tech Notes

- Soft deletes are used for users/tasks/comments (`is_deleted` flag) so history/audit data is
  preserved.
- `NotificationService` and `ActivityLogService` broadcast every new record over
  `/topic/notifications/{userId}` and `/topic/activity` respectively, in addition to
  persisting to MySQL — this powers the real-time UI without any polling.
