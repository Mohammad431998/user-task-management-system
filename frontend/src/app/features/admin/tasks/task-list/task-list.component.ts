import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { TaskService } from '../../../../core/services/task.service';
import { UserService } from '../../../../core/services/user.service';
import { Task, TaskStatus } from '../../../../core/models/task.model';
import { User } from '../../../../core/models/user.model';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterLink],
  templateUrl: './task-list.component.html',
  styleUrl: './task-list.component.scss'
})
export class TaskListComponent implements OnInit {
  private taskService = inject(TaskService);
  private userService = inject(UserService);
  private fb = inject(FormBuilder);

  tasks: Task[] = [];
  filteredTasks: Task[] = [];
  users: User[] = [];
  loading = true;

  statusFilter = '';
  userFilter = '';
  searchTerm = '';

  showModal = false;
  editingTask: Task | null = null;
  saving = false;
  errorMessage = '';

  statuses: TaskStatus[] = ['PENDING', 'IN_PROGRESS', 'COMPLETED'];

  form = this.fb.group({
    title: ['', Validators.required],
    description: [''],
    assignedUserId: [null as number | null, Validators.required],
    status: ['PENDING' as TaskStatus, Validators.required],
    dueDate: ['']
  });

  ngOnInit(): void {
    this.userService.getAll().subscribe((users) => (this.users = users));
    this.loadTasks();
  }

  loadTasks(): void {
    this.loading = true;
    this.taskService.getAll().subscribe((tasks) => {
      this.tasks = tasks;
      this.applyFilters();
      this.loading = false;
    });
  }

  applyFilters(): void {
    this.filteredTasks = this.tasks.filter((t) => {
      const matchesStatus = !this.statusFilter || t.status === this.statusFilter;
      const matchesUser = !this.userFilter || t.assignedUserId === Number(this.userFilter);
      const matchesSearch =
        !this.searchTerm || t.title.toLowerCase().includes(this.searchTerm.toLowerCase());
      return matchesStatus && matchesUser && matchesSearch;
    });
  }

  openCreateModal(): void {
    this.editingTask = null;
    this.errorMessage = '';
    this.form.reset({
      title: '',
      description: '',
      assignedUserId: null,
      status: 'PENDING',
      dueDate: ''
    });
    this.showModal = true;
  }

  openEditModal(task: Task): void {
    this.editingTask = task;
    this.errorMessage = '';
    this.form.reset({
      title: task.title,
      description: task.description,
      assignedUserId: task.assignedUserId,
      status: task.status,
      dueDate: task.dueDate ?? ''
    });
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving = true;
    this.errorMessage = '';
    const value = this.form.getRawValue();
    const payload = {
      title: value.title!,
      description: value.description || '',
      assignedUserId: value.assignedUserId!,
      status: value.status!,
      dueDate: value.dueDate || null
    };

    const request$ = this.editingTask
      ? this.taskService.update(this.editingTask.id, payload)
      : this.taskService.create(payload);

    request$.subscribe({
      next: () => {
        this.saving = false;
        this.showModal = false;
        this.loadTasks();
      },
      error: (err: HttpErrorResponse) => {
        this.saving = false;
        this.errorMessage = err.error?.message || 'Failed to save task.';
      }
    });
  }

  deleteTask(task: Task): void {
    if (!confirm(`Delete task "${task.title}"?`)) return;
    this.taskService.delete(task.id).subscribe(() => this.loadTasks());
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
