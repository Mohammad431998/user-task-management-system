export type TaskStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED';

export interface Task {
  id: number;
  title: string;
  description: string;
  assignedUserId: number;
  assignedUserName: string;
  status: TaskStatus;
  dueDate: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface TaskRequest {
  title: string;
  description: string;
  assignedUserId: number;
  status: TaskStatus;
  dueDate: string | null;
}

export interface AssignTaskRequest {
  userId: number;
}

export interface TaskStatusUpdateRequest {
  status: TaskStatus;
}
