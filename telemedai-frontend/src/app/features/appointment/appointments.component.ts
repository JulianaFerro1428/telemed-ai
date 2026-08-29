import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { IonicModule, AlertController } from '@ionic/angular';
import { AppointmentService } from '../../core/services/appointment.service';
import { AuthService } from '../../core/services/auth.service';
import { Appointment, AppointmentStatus } from '../../core/models';
import { ShellComponent } from '../../shared/components/shell/shell.component';
import { LoadingComponent } from '../../shared/components/loading/loading.component';

@Component({
  selector: 'app-appointments',
  standalone: true,
  imports: [
    CommonModule,
    IonicModule,
    RouterLink,
    ShellComponent,
    LoadingComponent
  ],
  templateUrl: './appointments.component.html',
  styleUrl: './appointments.component.scss'
})
export class AppointmentsComponent {
  private readonly service = inject(AppointmentService);
  private readonly auth = inject(AuthService);
  private readonly alerts = inject(AlertController);

  loading = true;
  appointments: Appointment[] = [];
  filter: 'TODAS' | AppointmentStatus = 'TODAS';

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    const id = this.auth.getProfileId(); // ✅ Cambio
    if (!id) {
      this.loading = false;
      return;
    }

    this.service.byPatient(id).subscribe({
      next: (a) => {
        this.appointments = a;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  get filtered(): Appointment[] {
    return this.filter === 'TODAS'
      ? this.appointments
      : this.appointments.filter((a) => a.status === this.filter);
  }

  statusClass(s: string): string {
    return 'status status-' + s.toLowerCase().replace('_', '-');
  }

  async cancel(a: Appointment): Promise<void> {
    const alert = await this.alerts.create({
      header: 'Cancelar cita',
      message: 'Indica el motivo de cancelación.',
      inputs: [
        {
          name: 'reason',
          type: 'text',
          placeholder: 'Motivo'
        }
      ],
      buttons: [
        {
          text: 'Volver',
          role: 'cancel'
        },
        {
          text: 'Cancelar cita',
          role: 'destructive',
          handler: (data) => {
            if (!data.reason) return;
            this.service.cancel(a.id, data.reason).subscribe(() => this.load());
          }
        }
      ]
    });

    await alert.present();
  }
}