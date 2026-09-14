import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  Component,
  OnInit
} from '@angular/core';

import { RouterLink } from '@angular/router';

import { IonicModule } from '@ionic/angular';

import { addIcons } from 'ionicons';

import {
  searchOutline,
  notificationsOutline,
  personOutline,
  sendOutline,
  attachOutline,
  informationCircleOutline,
  sparklesOutline
} from 'ionicons/icons';

import { ShellComponent } from '../../shared/components/shell/shell.component';

import { AgentService } from '../../core/services/agent.service';

import {
  ChatMessage,
  PreconsultationSummaryResponse
} from '../../core/models/agent.models';

@Component({
  selector: 'app-preconsulta',

  standalone: true,

  imports: [
    CommonModule,
    IonicModule,
    RouterLink,
    FormsModule,
    ShellComponent
  ],

  templateUrl: './preconsulta.component.html',

  styleUrl: './preconsulta.component.scss'
})
export class PreconsultaComponent implements OnInit {

  /**
   * ID de la conversación creada por el backend.
   */
  conversationId: number | null = null;

  /**
   * Mensajes que se muestran visualmente
   * en el chat.
   */
  messages: ChatMessage[] = [];

  /**
   * Texto que está escribiendo actualmente
   * el paciente.
   */
  currentMessage = '';

  /**
   * Indica que estamos esperando respuesta
   * del backend / Groq.
   */
  loading = false;

  /**
   * Indica que las cinco respuestas
   * requeridas ya fueron recopiladas.
   */
  preconsultationComplete = false;

  /**
   * Indica que la sesión expiró.
   */
  sessionExpired = false;

  /**
   * Resumen generado al finalizar.
   */
  summary: PreconsultationSummaryResponse | null = null;

  constructor(
    private readonly agentService: AgentService
  ) {

    addIcons({
      'search-outline': searchOutline,
      'notifications-outline': notificationsOutline,
      'person-outline': personOutline,
      'send-outline': sendOutline,
      'attach-outline': attachOutline,
      'information-circle-outline': informationCircleOutline,
      'sparkles-outline': sparklesOutline
    });
  }

  /**
   * Cuando el usuario entra a la pantalla,
   * iniciamos automáticamente la preconsulta.
   */
  ngOnInit(): void {

    this.startConversation();
  }

  /**
   * Inicia una nueva conversación.
   *
   * El primer mensaje lo devuelve el backend,
   * por lo que no queda quemado en Angular.
   */
  private startConversation(): void {

    this.loading = true;

    this.agentService
      .startConversation()
      .subscribe({

        next: (response) => {

          this.conversationId =
            response.conversationId;

          /*
           * El AGENTE es el primero
           * que habla en la conversación.
           */
          this.messages.push({
            sender: 'AGENT',
            content: response.message,
            timestamp: new Date()
          });

          this.loading = false;
        },

        error: (error) => {

          console.error(
            'Error iniciando la preconsulta:',
            error
          );

          this.loading = false;
        }
      });
  }

  /**
   * Envía el mensaje escrito por el paciente.
   */
  sendMessage(): void {

    const message =
      this.currentMessage.trim();

    /*
     * No permitimos enviar mensajes vacíos.
     */
    if (!message) {
      return;
    }

    /*
     * La conversación debe existir.
     */
    if (this.conversationId === null) {
      return;
    }

    /*
     * Si ya terminó o expiró,
     * no aceptamos más mensajes.
     */
    if (
      this.preconsultationComplete ||
      this.sessionExpired
    ) {
      return;
    }

    /*
     * Pintamos inmediatamente el mensaje
     * del paciente.
     */
    this.messages.push({
      sender: 'PATIENT',
      content: message,
      timestamp: new Date()
    });

    /*
     * Limpiamos la caja de texto.
     */
    this.currentMessage = '';

    this.loading = true;

    this.agentService
      .sendMessage(
        this.conversationId,
        message
      )
      .subscribe({

        next: (response) => {

          /*
           * Mostramos la respuesta generada
           * por el agente.
           */
          this.messages.push({
            sender: 'AGENT',
            content: response.response,
            timestamp: new Date()
          });

          this.preconsultationComplete =
            response.preconsultationComplete;

          this.loading = false;

          /*
           * Si ya tenemos las cinco respuestas,
           * generamos automáticamente el resumen.
           */
          if (response.preconsultationComplete) {

            this.finishConversation();
          }
        },

        error: (error) => {

          console.error(
            'Error enviando mensaje:',
            error
          );

          this.loading = false;

          /*
           * Si el backend informa que la sesión
           * expiró, bloqueamos el chat.
           */
          const backendMessage =
            error?.error?.message ?? '';

          if (
            backendMessage
              .toLowerCase()
              .includes('expir')
          ) {

            this.sessionExpired = true;

            this.messages.push({
              sender: 'AGENT',
              content:
                'La sesión de preconsulta finalizó por inactividad. Puedes iniciar una nueva preconsulta para continuar.',
              timestamp: new Date()
            });
          }
        }
      });
  }

  /**
   * Finaliza la conversación y solicita
   * al backend el resumen estructurado
   * destinado al profesional de salud.
   */
  private finishConversation(): void {

    if (this.conversationId === null) {
      return;
    }

    this.loading = true;

    this.agentService
      .finishConversation(
        this.conversationId
      )
      .subscribe({

        next: (summary) => {

          this.summary = summary;

          this.loading = false;

          console.log(
            'Preconsulta finalizada:',
            summary
          );
        },

        error: (error) => {

          console.error(
            'Error finalizando la preconsulta:',
            error
          );

          this.loading = false;
        }
      });
  }

  /**
   * Permite enlazar posteriormente
   * el input HTML con el componente.
   */
  updateMessage(
    value: string | null | undefined
  ): void {

    this.currentMessage =
      value ?? '';
  }
}