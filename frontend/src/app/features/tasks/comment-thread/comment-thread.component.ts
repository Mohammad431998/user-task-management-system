import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ThreadedComment } from '../../../core/models/comment.model';

@Component({
  selector: 'app-comment-thread',
  standalone: true,
  imports: [CommonModule, CommentThreadComponent],
  templateUrl: './comment-thread.component.html',
  styleUrl: './comment-thread.component.scss'
})
export class CommentThreadComponent {
  @Input({ required: true }) comments: ThreadedComment[] = [];
  @Input() isAdmin = false;
  @Input() depth = 0;

  @Output() reply = new EventEmitter<ThreadedComment>();
  @Output() remove = new EventEmitter<ThreadedComment>();

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleString();
  }

  initials(name: string): string {
    return name
      .split(' ')
      .map((p) => p.charAt(0))
      .slice(0, 2)
      .join('')
      .toUpperCase();
  }
}
