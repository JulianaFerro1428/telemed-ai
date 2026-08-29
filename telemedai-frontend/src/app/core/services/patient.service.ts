import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Patient } from '../models';

@Injectable({
  providedIn: 'root'
})
export class PatientService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiUrl;

  get(id: number): Observable<Patient> {
    return this.http.get<Patient>(`${this.base}/patients/${id}`);
  }

  update(
    id: number,
    payload: {
      fullName: string;
      phone?: string;
      birthDate?: string;
      medicalHistory?: string;
    }
  ): Observable<Patient> {
    return this.http.put<Patient>(`${this.base}/patients/${id}`, payload);
  }

  downloadHistory(id: number): Observable<Blob> {
    return this.http.get(`${this.base}/patients/me/history/${id}/download`, {
      responseType: 'blob'
    });
  }
}