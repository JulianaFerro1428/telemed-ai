import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { IonicModule } from '@ionic/angular';

import { addIcons } from 'ionicons';

import {
  searchOutline,
  notificationsOutline,
  arrowForwardOutline,
  checkmarkOutline,
  calendarOutline,
  warningOutline
} from 'ionicons/icons';

import { ShellComponent } from '../../shared/components/shell/shell.component';
import { LoadingComponent } from '../../shared/components/loading/loading.component';

import { AppointmentService } from '../../core/services/appointment.service';
import { PatientService } from '../../core/services/patient.service';
import { NotificationService } from '../../core/services/notification.service';
import { AuthService } from '../../core/services/auth.service';

import {
  Appointment,
  Patient,
  Notification
} from '../../core/models';


@Component({
  selector: 'app-dashboard',

  standalone: true,

  imports: [
    CommonModule,
    IonicModule,
    RouterLink,
    ShellComponent,
    LoadingComponent
  ],

  templateUrl: './dashboard.component.html',

  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent {

  private readonly appointments = inject(AppointmentService);

  private readonly patients = inject(PatientService);

  private readonly notifications = inject(NotificationService);

  private readonly auth = inject(AuthService);


  loading = true;

  patient: Patient | null = null;

  upcoming: Appointment[] = [];

  allAppointments: Appointment[] = [];

  recentNotifications: Notification[] = [];

  completedCount = 0;

  unreadCount = 0;

  error = '';


  constructor() {

    addIcons({
      'search-outline': searchOutline,
      'notifications-outline': notificationsOutline,
      'arrow-forward-outline': arrowForwardOutline,
      'checkmark-outline': checkmarkOutline,
      'calendar-outline': calendarOutline,
      'warning-outline': warningOutline
    });

  }


  ngOnInit(): void {

    const role = this.auth.getRole();

    if (role !== 'PACIENTE') {

      this.loading = false;

      this.error =
        'Esta pantalla solo está disponible para pacientes.';

      return;

    }


    const patientId = this.auth.getPatientId();

    const userId = this.auth.getUserId();


    if (!patientId) {

      this.loading = false;

      this.error =
        'No se encontró el ID del perfil del paciente.';

      return;

    }


    /* ===============================
       Cargar paciente
    =============================== */

    this.patients.get(patientId).subscribe({

      next: (patient) => {

        this.patient = patient;

      },

      error: () => {

        this.error =
          'No fue posible cargar el perfil del paciente.';

      }

    });


    /* ===============================
       Cargar citas
    =============================== */

    this.appointments.byPatient(patientId).subscribe({

      next: (appointments) => {

        this.allAppointments = appointments;


        /* Consultas completadas */

        this.completedCount =
          appointments.filter(
            appointment =>
              appointment.status === 'COMPLETADA'
          ).length;


        /* Próximas citas */

        this.upcoming = appointments

          .filter(
            appointment =>
              ['CONFIRMADA', 'REPROGRAMADA']
                .includes(appointment.status)
          )

          .filter(
            appointment =>
              new Date(appointment.startTime).getTime() >=
              new Date().getTime()
          )

          .sort(
            (a, b) =>
              new Date(a.startTime).getTime() -
              new Date(b.startTime).getTime()
          )

          .slice(0, 3);


        this.loading = false;

      },

      error: () => {

        this.loading = false;

        this.error =
          'No fue posible cargar las citas.';

      }

    });


    /* ===============================
       Cargar notificaciones
    =============================== */

    if (userId) {

      this.notifications.byUser(userId).subscribe({

        next: (notifications) => {

          this.unreadCount =
            notifications.filter(
              notification => !notification.read
            ).length;


          this.recentNotifications =
            [...notifications]

              .sort(
                (a, b) =>
                  new Date(b.sentAt).getTime() -
                  new Date(a.sentAt).getTime()
              )

              .slice(0, 4);

        },

        error: () => {

          this.unreadCount = 0;

          this.recentNotifications = [];

        }

      });

    }

  }


  /* =========================================
     PRÓXIMA CITA
  ========================================== */

  get nextAppointment(): Appointment | null {

    return this.upcoming.length
      ? this.upcoming[0]
      : null;

  }


  /* =========================================
     NOMBRE
  ========================================== */

  getName(): string {

    const name =
      this.patient?.user?.fullName ||
      this.auth.getCurrentUser()?.fullName ||
      'Paciente';

    /*
      Para que el saludo no quede demasiado largo,
      mostramos el primer nombre.
    */

    return name.split(' ')[0];

  }


  /* =========================================
     INICIALES
  ========================================== */

  getInitials(): string {

    const name =
      this.patient?.user?.fullName ||
      this.auth.getCurrentUser()?.fullName ||
      'Paciente';


    const parts =
      name
        .trim()
        .split(' ')
        .filter(Boolean);


    if (parts.length === 1) {

      return parts[0]
        .substring(0, 2)
        .toUpperCase();

    }


    return (
      parts[0][0] +
      parts[parts.length - 1][0]
    ).toUpperCase();

  }


  /* =========================================
     ESTADO
  ========================================== */

  statusClass(status: string): string {

    return (
      'status status-' +
      status
        .toLowerCase()
        .replace('_', '-')
    );

  }


  /* =========================================
     FECHA
  ========================================== */

  formatDate(date: string): string {

    return new Intl.DateTimeFormat(
      'es-CO',
      {
        weekday: 'short',
        day: '2-digit',
        month: 'short'
      }
    ).format(new Date(date));

  }

}