import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { IonicModule, ToastController } from '@ionic/angular';
import { AppointmentService } from '../../core/services/appointment.service';
import { PatientService } from '../../core/services/patient.service';
import { AuthService } from '../../core/services/auth.service';
import { SummaryService } from '../../core/services/summary.service';
import { Appointment, PostSummary } from '../../core/models';
import { ShellComponent } from '../../shared/components/shell/shell.component';
import { LoadingComponent } from '../../shared/components/loading/loading.component';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [
    CommonModule,
    IonicModule,
    ShellComponent,
    LoadingComponent
  ],
  templateUrl: './history.component.html',
  styleUrl: './history.component.scss'
})
export class HistoryComponent {
  private readonly appointments = inject(AppointmentService);
  private readonly summaries = inject(SummaryService);
  private readonly patients = inject(PatientService);
  private readonly auth = inject(AuthService);
  private readonly toast = inject(ToastController);

  loading = true;
  items: Appointment[] = [];
  selected: PostSummary | null = null;
  selecting = false;

  ngOnInit(): void {
    const id = this.auth.getProfileId(); // ✅ Cambio

    if (!id) {
      this.loading = false;
      return;
    }

    this.appointments.byPatient(id).subscribe({
      next: (a) => {
        this.items = a.filter((x) => x.status === 'COMPLETADA');
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  open(a: Appointment): void {
    if (!a.postSummaryId) {
      this.selected = null;
      this.selecting = true;
      return;
    }

    this.selecting = true;
    this.summaries.getPost(a.postSummaryId).subscribe({
      next: (s) => {
        this.selected = s;
      },
      error: () => {
        this.selected = null;
      }
    });
  }

  download(a: Appointment): void {
    this.patients.downloadHistory(a.id).subscribe({
      next: (b) => {
        const url = URL.createObjectURL(b);
        const link = document.createElement('a');
        link.href = url;
        link.download = `telemed-resumen-${a.id}.pdf`;
        link.click();
        URL.revokeObjectURL(url);
      },
      error: () => {
        void this.toast
          .create({
            message:
              'El endpoint de PDF aún está pendiente en el backend. El resumen sí está disponible en pantalla.',
            duration: 3500,
            color: 'warning'
          })
          .then((t) => t.present());
      }
    });
  }
}