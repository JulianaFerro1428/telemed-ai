import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { AppointmentService } from '../../core/services/appointment.service';
import { ProfessionalService } from '../../core/services/professional.service';
import { AuthService } from '../../core/services/auth.service';
import { Appointment, Professional } from '../../core/models';
import { ShellComponent } from '../../shared/components/shell/shell.component';
import { LoadingComponent } from '../../shared/components/loading/loading.component';

@Component({
  selector: 'app-schedule',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    ShellComponent,
    LoadingComponent
  ],
  templateUrl: './schedule.component.html',
  styleUrl: './schedule.component.scss'
})
export class ScheduleComponent {
  private readonly appointments = inject(AppointmentService);
  private readonly professionals = inject(ProfessionalService);
  private readonly auth = inject(AuthService);

  loading = true;
  items: Appointment[] = [];
  professional: Professional | null = null;
  profileId: number | null = null;

  ngOnInit(): void {
  this.profileId = this.auth.getProfileId();
  console.log('🔍 ProfileId:', this.profileId); // Depuración
  this.load();
}

  load(): void {
    if (!this.profileId) {
      this.loading = false;
      return;
    }

    this.loading = true;

    this.professionals.get(this.profileId).subscribe({
      next: (p) => {
        this.professional = p;
      },
      error: () => {
        this.professional = null;
      }
    });

    this.appointments.byProfessional(this.profileId).subscribe({
      next: (a) => {
        this.items = a;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  saveId(): void {
    if (this.profileId) {
      this.load();
    }
  }

  get today(): Appointment[] {
    return this.items.filter(
      (a) =>
        new Date(a.startTime).toDateString() === new Date().toDateString()
    );
  }
}