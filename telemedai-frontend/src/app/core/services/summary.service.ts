import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AttentionSummary, PostSummary, PreconsultationSummary } from '../models';

@Injectable({
  providedIn: 'root'
})
export class SummaryService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiUrl;

  createPre(payload: {
    appointmentId: number;
    consultationReason: string;
    evolutionTime?: string;
    detailedSymptoms: string;
    relevantHistory?: string;
  }): Observable<PreconsultationSummary> {
    return this.http.post<PreconsultationSummary>(
      `${this.base}/summaries/preconsultation`,
      payload
    );
  }

  getPre(id: number): Observable<PreconsultationSummary> {
    return this.http.get<PreconsultationSummary>(`${this.base}/summaries/preconsultation/${id}`);
  }

  createAttention(payload: {
    appointmentId: number;
    diagnosis: string;
    recommendations: string;
    medications?: string;
    observations?: string;
    referral?: string;
  }): Observable<AttentionSummary> {
    return this.http.post<AttentionSummary>(`${this.base}/summaries/attention`, payload);
  }

  getAttention(id: number): Observable<AttentionSummary> {
    return this.http.get<AttentionSummary>(`${this.base}/summaries/attention/${id}`);
  }

  createPost(payload: {
    appointmentId: number;
    preconsultationSummaryId: number;
    attentionSummaryId: number;
  }): Observable<PostSummary> {
    return this.http.post<PostSummary>(`${this.base}/summaries/post`, payload);
  }

  getPost(id: number): Observable<PostSummary> {
    return this.http.get<PostSummary>(`${this.base}/summaries/post/${id}`);
  }
}