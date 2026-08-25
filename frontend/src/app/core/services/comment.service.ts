import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Comment, CommentRequest, ThreadedComment } from '../models/comment.model';

@Injectable({ providedIn: 'root' })
export class CommentService {
  private readonly baseUrl = `${environment.apiUrl}/comments`;

  constructor(private http: HttpClient) {}

  getByTask(taskId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.baseUrl}/task/${taskId}`);
  }

  create(request: CommentRequest): Observable<Comment> {
    return this.http.post<Comment>(this.baseUrl, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  /** Builds a threaded (top-level + nested replies) tree from a flat comment list. */
  static buildThread(comments: Comment[]): ThreadedComment[] {
    const byId = new Map<number, ThreadedComment>();
    comments.forEach((c) => byId.set(c.id, { ...c, replies: [] }));

    const roots: ThreadedComment[] = [];
    byId.forEach((comment) => {
      if (comment.parentCommentId && byId.has(comment.parentCommentId)) {
        byId.get(comment.parentCommentId)!.replies.push(comment);
      } else {
        roots.push(comment);
      }
    });

    const sortByDate = (a: Comment, b: Comment) =>
      new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime();

    const sortRecursive = (nodes: ThreadedComment[]) => {
      nodes.sort(sortByDate);
      nodes.forEach((n) => sortRecursive(n.replies));
    };
    sortRecursive(roots);

    return roots;
  }
}
