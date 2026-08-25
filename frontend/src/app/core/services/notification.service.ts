import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client/dist/sockjs';
import { environment } from '../../../environments/environment';
import { AppNotification } from '../models/notification.model';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly baseUrl = `${environment.apiUrl}/notifications`;
  private client: Client | null = null;

  notifications = signal<AppNotification[]>([]);
  unreadCount = signal<number>(0);

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  loadInitial(userId: number): void {
    this.http
      .get<AppNotification[]>(`${this.baseUrl}/user/${userId}`)
      .subscribe((list) => {
        this.notifications.set(list);
        this.unreadCount.set(list.filter((n) => !n.read).length);
      });
  }

  markAsRead(id: number): void {
    this.http.patch<AppNotification>(`${this.baseUrl}/${id}/read`, {}).subscribe((updated) => {
      this.notifications.update((list) =>
        list.map((n) => (n.id === updated.id ? updated : n))
      );
      this.unreadCount.update((count) => Math.max(0, count - 1));
    });
  }

  markAllAsRead(): void {
    this.notifications().forEach((n) => {
      if (!n.read) this.markAsRead(n.id);
    });
  }

  /** Opens a STOMP-over-SockJS connection and subscribes to the current user's channel. */
  connect(): void {
    const user = this.authService.currentUser();
    if (!user || this.client) return;

    this.client = new Client({
      webSocketFactory: () => new SockJS(environment.wsUrl),
      reconnectDelay: 5000,
      onConnect: () => {
        this.client?.subscribe(`/topic/notifications/${user.userId}`, (message: IMessage) => {
          const notification: AppNotification = JSON.parse(message.body);
          this.notifications.update((list) => [notification, ...list]);
          this.unreadCount.update((count) => count + 1);
        });
      }
    });

    this.client.activate();
  }

  disconnect(): void {
    this.client?.deactivate();
    this.client = null;
  }
}
