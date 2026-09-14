export interface StartConversationResponse {
  conversationId: number;
  status: 'ACTIVA' | 'FINALIZADA' | 'EXPIRADA';
  message: string;
}

export interface SendMessageResponse {
  conversationId: number;
  response: string;
  status: 'ACTIVA' | 'FINALIZADA' | 'EXPIRADA';
  preconsultationComplete: boolean;
}

export interface PreconsultationSummaryResponse {
  summaryId: number;
  consultationReason: string;
  evolutionTime: string;
  detailedSymptoms: string;
  relevantHistory: string;
}

export interface ChatMessage {
  sender: 'PATIENT' | 'AGENT';
  content: string;
  timestamp: Date;
}