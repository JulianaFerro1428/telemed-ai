import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Professional } from '../models';

@Injectable({
  providedIn: 'root'
})
export class ProfessionalService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiUrl;

  list(): Observable<Professional[]> {
    return this.http.get<Professional[]>(`${this.base}/professionals`);
  }

  get(id: number): Observable<Professional> {
    return this.http.get<Professional>(`${this.base}/professionals/${id}`);
  }

  create(payload: {
    fullName: string;
    email: string;
    identityDocument: string;
    password: string;
    licenseNumber: string;
    specialtyId: number;
    yearsExperience: number;
  }): Observable<Professional> {
    return this.http.post<Professional>(
      `${this.base}/professionals/admin/register`,
      payload
    );
  }
}