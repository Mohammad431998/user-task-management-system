import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { UserService } from '../../../core/services/user.service';
import { TaskService } from '../../../core/services/task.service';
import { User } from '../../../core/models/user.model';
import { Task } from '../../../core/models/task.model';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  loading = true;
  totalUsers = 0;
  activeUsers = 0;
  totalTasks = 0;
  pendingTasks = 0;
  inProgressTasks = 0;
  completedTasks = 0;
  recentTasks: Task[] = [];

  constructor(
    private userService: UserService,
    private taskService: TaskService
  ) {}

  ngOnInit(): void {
    forkJoin({
      users: this.userService.getAll(),
      tasks: this.taskService.getAll()
    }).subscribe(({ users, tasks }) => {
      this.computeUserStats(users);
      this.computeTaskStats(tasks);
      this.loading = false;
    });
  }

  private computeUserStats(users: User[]): void {
    this.totalUsers = users.length;
    this.activeUsers = users.filter((u) => u.status === 'ACTIVE').length;
  }

  private computeTaskStats(tasks: Task[]): void {
    this.totalTasks = tasks.length;
    this.pendingTasks = tasks.filter((t) => t.status === 'PENDING').length;
    this.inProgressTasks = tasks.filter((t) => t.status === 'IN_PROGRESS').length;
    this.completedTasks = tasks.filter((t) => t.status === 'COMPLETED').length;
    this.recentTasks = [...tasks]
      .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
      .slice(0, 5);
  }

  completionRate(): number {
    return this.totalTasks === 0 ? 0 : Math.round((this.completedTasks / this.totalTasks) * 100);
  }
}
