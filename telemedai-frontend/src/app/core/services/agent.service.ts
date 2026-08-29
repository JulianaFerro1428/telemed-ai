import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Conversation, Message } from '../models';

@Injectable({
  providedIn: 'root'
})
export class AgentService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiUrl;

  start(patientId: number): Observable<Conversation> {
    return this.http.post<Conversation>(`${this.base}/agent/conversations`, { patientId });
  }

  message(id: number, content: string): Observable<Message> {
    return this.http.post<Message>(`${this.base}/agent/conversations/${id}/messages`, { content });
  }

  finish(id: number): Observable<Conversation> {
    return this.http.post<Conversation>(`${this.base}/agent/conversations/${id}/finish`, {});
  }

  get(id: number): Observable<Conversation> {
    return this.http.get<Conversation>(`${this.base}/agent/conversations/${id}`);
  }

  byPatient(id: number): Observable<Conversation[]> {
    return this.http.get<Conversation[]>(`${this.base}/agent/patients/${id}/conversations`);
  }
}