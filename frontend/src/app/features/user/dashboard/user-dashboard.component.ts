import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TaskService } from '../../../core/services/task.service';
import { Task, TaskStatus } from '../../../core/models/task.model';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './user-dashboard.component.html',
  styleUrl: './user-dashboard.component.scss'
})
export class UserDashboardComponent implements OnInit {
  tasks: Task[] = [];
  loading = true;
  statusFilter = '';

  constructor(private taskService: TaskService) {}

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.loading = true;
    this.taskService.getMyTasks().subscribe((tasks) => {
      this.tasks = tasks;
      this.loading = false;
    });
  }

  get filteredTasks(): Task[] {
    return this.statusFilter
      ? this.tasks.filter((t) => t.status === this.statusFilter)
      : this.tasks;
  }

  quickUpdateStatus(task: Task, status: string): void {
    this.taskService.updateStatus(task.id, { status: status as TaskStatus }).subscribe((updated) => {
      task.status = updated.status;
    });
  }

  get pendingCount(): number {
    return this.tasks.filter((t) => t.status === 'PENDING').length;
  }

  get inProgressCount(): number {
    return this.tasks.filter((t) => t.status === 'IN_PROGRESS').length;
  }

  get completedCount(): number {
    return this.tasks.filter((t) => t.status === 'COMPLETED').length;
  }

  statusBadgeClass(status: TaskStatus): string {
    switch (status) {
      case 'COMPLETED':
        return 'text-bg-success';
      case 'IN_PROGRESS':
        return 'text-bg-info';
      default:
        return 'text-bg-warning';
    }
  }
}
