import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client/dist/sockjs';
import { environment } from '../../../../environments/environment';
import { ActivityLogService } from '../../../core/services/activity-log.service';
import { ActivityLog } from '../../../core/models/activity-log.model';

@Component({
  selector: 'app-activity-log',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './activity-log.component.html',
  styleUrl: './activity-log.component.scss'
})
export class ActivityLogComponent implements OnInit, OnDestroy {
  logs: ActivityLog[] = [];
  loading = true;
  private client: Client | null = null;

  constructor(private activityLogService: ActivityLogService) {}

  ngOnInit(): void {
    this.activityLogService.getAll().subscribe((logs) => {
      this.logs = logs.sort(
        (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
      );
      this.loading = false;
    });

    this.connectLiveFeed();
  }

  ngOnDestroy(): void {
    this.client?.deactivate();
  }

  private connectLiveFeed(): void {
    this.client = new Client({
      webSocketFactory: () => new SockJS(environment.wsUrl),
      reconnectDelay: 5000,
      onConnect: () => {
        this.client?.subscribe('/topic/activity', (message: IMessage) => {
          const log: ActivityLog = JSON.parse(message.body);
          this.logs = [log, ...this.logs];
        });
      }
    });
    this.client.activate();
  }

  actionBadgeClass(action: string): string {
    if (action.includes('DELETE')) return 'text-bg-danger';
    if (action.includes('CREATE')) return 'text-bg-success';
    if (action.includes('LOGIN')) return 'text-bg-primary';
    if (action.includes('LOGOUT')) return 'text-bg-secondary';
    return 'text-bg-info';
  }
}
