import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { NotificationService } from '../../core/services/notification.service';
import { AuthService } from '../../core/services/auth.service';
import { Notification } from '../../core/models';
import { ShellComponent } from '../../shared/components/shell/shell.component';
import { LoadingComponent } from '../../shared/components/loading/loading.component';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [
    CommonModule,
    IonicModule,
    ShellComponent,
    LoadingComponent
  ],
  templateUrl: './notifications.component.html',
  styleUrl: './notifications.component.scss'
})
export class NotificationsComponent {
  private readonly service = inject(NotificationService);
  private readonly auth = inject(AuthService);

  loading = true;
  items: Notification[] = [];

  ngOnInit(): void {
    const id = this.auth.getUserId(); // ← Mantiene getUserId()
    if (!id) {
      this.loading = false;
      return;
    }

    this.service.byUser(id).subscribe({
      next: (n) => {
        this.items = n;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }
}