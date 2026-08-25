export interface ActivityLog {
  id: number;
  userId: number | null;
  action: string;
  entityType: string;
  entityId: number | null;
  description: string;
  createdAt: string;
}
