import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { IonicModule, ToastController } from '@ionic/angular';
import { ProfessionalService } from '../../core/services/professional.service';
import { AppointmentService } from '../../core/services/appointment.service';
import { AuthService } from '../../core/services/auth.service';
import { Professional } from '../../core/models';
import { ShellComponent } from '../../shared/components/shell/shell.component';
import { LoadingComponent } from '../../shared/components/loading/loading.component';

@Component({
  selector: 'app-book-appointment',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    IonicModule,
    RouterLink,
    ShellComponent,
    LoadingComponent
  ],
  templateUrl: './book-appointment.component.html',
  styleUrl: './book-appointment.component.scss'
})
export class BookAppointmentComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly professionals = inject(ProfessionalService);
  private readonly appointments = inject(AppointmentService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly toast = inject(ToastController);

  loading = true;
  saving = false;
  list: Professional[] = [];
  specialties: string[] = [];

  appointmentId: number | null = null;
  isReschedule = false;

  form = this.fb.nonNullable.group({
    professionalId: [0, [Validators.required, Validators.min(1)]],
    start: ['', [Validators.required]],
    end: ['', [Validators.required]]
  });

  ngOnInit(): void {
    this.appointmentId = Number(this.route.snapshot.paramMap.get('id')) || null;
    this.isReschedule = !!this.appointmentId;

    this.professionals.list().subscribe({
      next: (p) => {
        this.list = p;
        this.specialties = [...new Set(p.map((x) => x.specialty?.name).filter(Boolean) as string[])];
        this.loading = false;

        if (this.isReschedule && this.appointmentId) {
          this.loadAppointmentData(this.appointmentId);
        }
      },
      error: () => {
        this.loading = false;
        this.showError('No se pudieron cargar los profesionales.');
      }
    });
  }

  private loadAppointmentData(id: number): void {
    this.appointments.get(id).subscribe({
      next: (app) => {
        this.form.patchValue({
          professionalId: app.professional.id,
          start: this.formatDateTimeLocal(app.startTime),
          end: this.formatDateTimeLocal(app.endTime)
        });
        this.loading = false;
      },
      error: () => {
        this.showError('No se pudo cargar la cita para reprogramar.');
        this.loading = false;
      }
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.showError('Completa todos los campos correctamente.');
      this.form.markAllAsTouched();
      return;
    }

    const v = this.form.getRawValue();
    const start = new Date(v.start);
    const end = new Date(v.end);

    const now = new Date();
    if (start.getTime() <= now.getTime()) {
      this.showError('La fecha de inicio debe ser futura.');
      return;
    }
    if (end <= start) {
      this.showError('La fecha de fin debe ser posterior a la de inicio.');
      return;
    }

    const patientId = this.auth.getProfileId(); // ✅ Cambio
    if (!patientId) {
      this.showError('No se encontró tu perfil de paciente.');
      return;
    }

    const professionalId = Number(v.professionalId);
    if (!professionalId || professionalId < 1) {
      this.showError('Selecciona un profesional válido.');
      return;
    }

    this.saving = true;

    if (this.isReschedule && this.appointmentId) {
      this.appointments
        .reschedule(this.appointmentId, start.toISOString(), end.toISOString())
        .subscribe({
          next: () => {
            this.saving = false;
            this.showSuccess('Cita reprogramada exitosamente.');
            void this.router.navigate(['/appointments']);
          },
          error: (err) => {
            this.saving = false;
            const msg = err.error?.message || 'No se pudo reprogramar la cita.';
            this.showError(msg);
          }
        });
    } else {
      this.appointments
        .create({
          patientId,
          professionalId,
          start: start.toISOString(),
          end: end.toISOString()
        })
        .subscribe({
          next: () => {
            this.saving = false;
            this.showSuccess('Cita agendada exitosamente.');
            void this.router.navigate(['/appointments']);
          },
          error: (err) => {
            this.saving = false;
            const msg = err.error?.message || 'No se pudo agendar la cita.';
            this.showError(msg);
          }
        });
    }
  }

  private async showError(message: string): Promise<void> {
    const toast = await this.toast.create({
      message,
      duration: 4000,
      color: 'danger',
      position: 'top',
      buttons: [{ text: 'OK', role: 'cancel' }]
    });
    await toast.present();
  }

  private async showSuccess(message: string): Promise<void> {
    const toast = await this.toast.create({
      message,
      duration: 3000,
      color: 'success',
      position: 'top'
    });
    await toast.present();
  }

  minDateTime(): string {
    const d = new Date(Date.now() + 15 * 60000);
    const pad = (n: number) => String(n).padStart(2, '0');
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
  }

  private formatDateTimeLocal(iso: string): string {
    const d = new Date(iso);
    const pad = (n: number) => String(n).padStart(2, '0');
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
  }
}