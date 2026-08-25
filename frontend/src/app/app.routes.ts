import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { homeGuard } from './core/guards/home.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then((m) => m.LoginComponent)
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./layout/shell/shell.component').then((m) => m.ShellComponent),
    children: [
      { path: '', pathMatch: 'full', canActivate: [homeGuard], children: [] },
      {
        path: 'profile',
        loadComponent: () =>
          import('./features/profile/profile.component').then((m) => m.ProfileComponent)
      },
      {
        path: 'admin',
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
        children: [
          {
            path: 'dashboard',
            loadComponent: () =>
              import('./features/admin/dashboard/dashboard.component').then(
                (m) => m.DashboardComponent
              )
          },
          {
            path: 'users',
            loadComponent: () =>
              import('./features/admin/users/user-list/user-list.component').then(
                (m) => m.UserListComponent
              )
          },
          {
            path: 'tasks',
            loadComponent: () =>
              import('./features/admin/tasks/task-list/task-list.component').then(
                (m) => m.TaskListComponent
              )
          },
          {
            path: 'tasks/:id',
            loadComponent: () =>
              import('./features/tasks/task-detail/task-detail.component').then(
                (m) => m.TaskDetailComponent
              )
          },
          {
            path: 'activity-log',
            loadComponent: () =>
              import('./features/admin/activity-log/activity-log.component').then(
                (m) => m.ActivityLogComponent
              )
          }
        ]
      },
      {
        path: 'user',
        canActivate: [roleGuard],
        data: { roles: ['USER'] },
        children: [
          {
            path: 'dashboard',
            loadComponent: () =>
              import('./features/user/dashboard/user-dashboard.component').then(
                (m) => m.UserDashboardComponent
              )
          },
          {
            path: 'tasks/:id',
            loadComponent: () =>
              import('./features/tasks/task-detail/task-detail.component').then(
                (m) => m.TaskDetailComponent
              )
          }
        ]
      }
    ]
  },
  { path: '**', redirectTo: 'login' }
];
