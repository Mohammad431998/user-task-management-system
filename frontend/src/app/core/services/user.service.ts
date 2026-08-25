import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ProfileUpdateRequest,
  RoleUpdateRequest,
  StatusUpdateRequest,
  User,
  UserRequest,
  UserStatus
} from '../models/user.model';
import { Role } from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly baseUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<User[]> {
    return this.http.get<User[]>(this.baseUrl);
  }

  search(term?: string, roleId?: number, status?: UserStatus): Observable<User[]> {
    let params = new HttpParams();
    if (term) params = params.set('search', term);
    if (roleId) params = params.set('roleId', roleId);
    if (status) params = params.set('status', status);
    return this.http.get<User[]>(`${this.baseUrl}/search`, { params });
  }

  getById(id: number): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/${id}`);
  }

  create(request: UserRequest): Observable<User> {
    return this.http.post<User>(this.baseUrl, request);
  }

  update(id: number, request: UserRequest): Observable<User> {
    return this.http.put<User>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  updateRole(id: number, request: RoleUpdateRequest): Observable<User> {
    return this.http.patch<User>(`${this.baseUrl}/${id}/role`, request);
  }

  updateStatus(id: number, request: StatusUpdateRequest): Observable<User> {
    return this.http.patch<User>(`${this.baseUrl}/${id}/status`, request);
  }

  getMyProfile(): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/me`);
  }

  updateMyProfile(request: ProfileUpdateRequest): Observable<User> {
    return this.http.put<User>(`${this.baseUrl}/me`, request);
  }

  getRoles(): Observable<Role[]> {
    return this.http.get<Role[]>(`${environment.apiUrl}/roles`);
  }
}
