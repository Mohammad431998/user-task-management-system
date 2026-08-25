export interface AppNotification {
  id: number;
  userId: number;
  taskId: number | null;
  title: string;
  message: string;
  type: string;
  read: boolean;
  createdAt: string;
}
