import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { TaskService } from '../../../core/services/task.service';
import { CommentService } from '../../../core/services/comment.service';
import { AuthService } from '../../../core/services/auth.service';
import { Task, TaskStatus } from '../../../core/models/task.model';
import { ThreadedComment } from '../../../core/models/comment.model';
import { CommentThreadComponent } from '../comment-thread/comment-thread.component';

@Component({
  selector: 'app-task-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, CommentThreadComponent],
  templateUrl: './task-detail.component.html',
  styleUrl: './task-detail.component.scss'
})
export class TaskDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private taskService = inject(TaskService);
  private commentService = inject(CommentService);
  public authService = inject(AuthService);
  private fb = inject(FormBuilder);

  task: Task | null = null;
  comments: ThreadedComment[] = [];
  loading = true;
  errorMessage = '';
  taskId!: number;

  statuses: TaskStatus[] = ['PENDING', 'IN_PROGRESS', 'COMPLETED'];
  replyTo: ThreadedComment | null = null;

  commentForm = this.fb.group({
    comment: ['', Validators.required]
  });

  postingComment = false;

  ngOnInit(): void {
    this.taskId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadTask();
    this.loadComments();
  }

  get isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  get backLink(): string {
    return this.isAdmin ? '/admin/tasks' : '/user/dashboard';
  }

  loadTask(): void {
    this.loading = true;
    this.taskService.getById(this.taskId).subscribe({
      next: (task) => {
        this.task = task;
        this.loading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Unable to load this task.';
      }
    });
  }

  loadComments(): void {
    this.commentService.getByTask(this.taskId).subscribe((comments) => {
      this.comments = CommentService.buildThread(comments);
    });
  }

  updateStatus(status: string): void {
    if (!this.task) return;
    this.taskService
      .updateStatus(this.taskId, { status: status as TaskStatus })
      .subscribe((updated) => (this.task = updated));
  }

  startReply(comment: ThreadedComment): void {
    this.replyTo = comment;
  }

  cancelReply(): void {
    this.replyTo = null;
  }

  postComment(): void {
    if (this.commentForm.invalid) {
      this.commentForm.markAllAsTouched();
      return;
    }

    this.postingComment = true;
    this.commentService
      .create({
        taskId: this.taskId,
        parentCommentId: this.replyTo?.id ?? null,
        comment: this.commentForm.value.comment!
      })
      .subscribe({
        next: () => {
          this.postingComment = false;
          this.commentForm.reset();
          this.replyTo = null;
          this.loadComments();
        },
        error: () => {
          this.postingComment = false;
        }
      });
  }

  deleteComment(comment: ThreadedComment): void {
    if (!confirm('Delete this comment?')) return;
    this.commentService.delete(comment.id).subscribe(() => this.loadComments());
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
