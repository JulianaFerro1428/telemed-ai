import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { AuthService } from '../../core/services/auth.service';
import { SessionService } from '../../core/services/session.service';
import { ShellComponent } from '../../shared/components/shell/shell.component';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule, IonicModule, ShellComponent],
  templateUrl: './users.component.html',
  styles: [
    `
      .notice,
      .current {
        padding: 22px;
        display: flex;
        gap: 15px;
        align-items: flex-start;
      }

      .notice ion-icon {
        font-size: 30px;
        color: #2c7be5;
      }

      .notice h2 {
        margin: 0;
        color: #172b4d;
      }

      .notice p {
        color: #68788f;
        line-height: 1.5;
      }

      .notice a {
        color: #2c7be5;
        font-weight: 700;
      }

      .current {
        align-items: center;
        margin-top: 15px;
      }

      .current span {
        font-size: 10px;
        color: #2c7be5;
        font-weight: 800;
      }

      .current h3 {
        margin: 4px 0;
        color: #172b4d;
      }

      .current p {
        margin: 0;
        color: #7a8798;
        font-size: 12px;
      }
    `
  ]
})
export class UsersComponent {
  readonly auth = inject(AuthService);
  readonly session = inject(SessionService);
}