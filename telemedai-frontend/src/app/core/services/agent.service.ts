import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  StartConversationResponse,
  SendMessageResponse,
  PreconsultationSummaryResponse
} from '../models/agent.models';

@Injectable({
  providedIn: 'root'
})
export class AgentService {

  /**
   * Endpoint del módulo Agent del backend.
   *
   * Más adelante podemos mover esta URL
   * a environment.ts.
   */
  private readonly apiUrl =
    'http://localhost:8080/api/agent';

  constructor(
    private readonly http: HttpClient
  ) {}

  /**
   * Inicia una nueva conversación.
   *
   * El frontend NO envía patientId.
   * El backend obtiene el paciente mediante el JWT.
   */
  startConversation():
    Observable<StartConversationResponse> {

    return this.http.post<StartConversationResponse>(
      `${this.apiUrl}/conversations`,
      {}
    );
  }

  /**
   * Envía un mensaje del paciente.
   */
  sendMessage(
    conversationId: number,
    message: string
  ): Observable<SendMessageResponse> {

    return this.http.post<SendMessageResponse>(
      `${this.apiUrl}/conversations/${conversationId}/messages`,
      {
        message
      }
    );
  }

  /**
   * Finaliza la preconsulta y solicita
   * la generación del resumen.
   */
  finishConversation(
    conversationId: number
  ): Observable<PreconsultationSummaryResponse> {

    return this.http.post<PreconsultationSummaryResponse>(
      `${this.apiUrl}/conversations/${conversationId}/finish`,
      {}
    );
  }
}