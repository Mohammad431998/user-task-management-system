import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ActivityLog } from '../models/activity-log.model';

@Injectable({ providedIn: 'root' })
export class ActivityLogService {
  private readonly baseUrl = `${environment.apiUrl}/activity-logs`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<ActivityLog[]> {
    return this.http.get<ActivityLog[]>(this.baseUrl);
  }

  getByUser(userId: number): Observable<ActivityLog[]> {
    return this.http.get<ActivityLog[]>(`${this.baseUrl}/user/${userId}`);
  }
}
