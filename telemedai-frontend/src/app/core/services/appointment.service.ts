import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Appointment, AppointmentStatus } from '../models';

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiUrl;

  create(payload: {
    patientId: number;
    professionalId: number;
    start: string;
    end: string;
  }): Observable<Appointment> {
    return this.http.post<Appointment>(`${this.base}/appointments`, payload);
  }

  get(id: number): Observable<Appointment> {
    return this.http.get<Appointment>(`${this.base}/appointments/${id}`);
  }

  byPatient(id: number): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${this.base}/appointments/patient/${id}`);
  }

  byProfessional(id: number): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${this.base}/appointments/professional/${id}`);
  }

  cancel(id: number, reason: string): Observable<Appointment> {
    return this.http.patch<Appointment>(`${this.base}/appointments/${id}/cancel`, { reason });
  }

  reschedule(id: number, start: string, end: string): Observable<Appointment> {
    return this.http.patch<Appointment>(`${this.base}/appointments/${id}/reschedule`, { start, end });
  }

  status(id: number, status: AppointmentStatus): Observable<Appointment> {
    return this.http.patch<Appointment>(`${this.base}/appointments/${id}/status`, { status });
  }
}