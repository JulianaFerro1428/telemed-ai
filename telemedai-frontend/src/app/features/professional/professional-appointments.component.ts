import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { AppointmentService } from '../../core/services/appointment.service';
import { AuthService } from '../../core/services/auth.service';
import { Appointment, AppointmentStatus } from '../../core/models';
import { ShellComponent } from '../../shared/components/shell/shell.component';
import { LoadingComponent } from '../../shared/components/loading/loading.component';

@Component({
  selector: 'app-professional-appointments',
  standalone: true,
  imports: [
    CommonModule,
    IonicModule,
    ShellComponent,
    LoadingComponent
  ],
  templateUrl: './professional-appointments.component.html',
  styleUrl: './professional-appointments.component.scss'
})
export class ProfessionalAppointmentsComponent {
  private readonly service = inject(AppointmentService);
  private readonly auth = inject(AuthService);

  loading = true;
  items: Appointment[] = [];

  ngOnInit(): void {
    const id = this.auth.getProfileId(); // ✅ Cambio

    if (!id) {
      this.loading = false;
      return;
    }

    this.service.byProfessional(id).subscribe({
      next: (a) => {
        this.items = a;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  setStatus(a: Appointment, s: AppointmentStatus): void {
    this.service.status(a.id, s).subscribe({
      next: (u) => {
        a.status = u.status;
      }
    });
  }
}