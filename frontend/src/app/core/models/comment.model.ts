export interface Comment {
  id: number;
  taskId: number;
  userId: number;
  userName: string;
  parentCommentId: number | null;
  comment: string;
  createdAt: string;
  updatedAt: string;
}

export interface CommentRequest {
  taskId: number;
  parentCommentId?: number | null;
  comment: string;
}

/** Comment with its replies nested, built client-side for threaded display. */
export interface ThreadedComment extends Comment {
  replies: ThreadedComment[];
}
