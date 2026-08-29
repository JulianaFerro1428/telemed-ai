export type Role = 'PACIENTE' | 'PROFESIONAL' | 'ADMIN';
export type AppointmentStatus = 'CONFIRMADA' | 'REPROGRAMADA' | 'COMPLETADA' | 'CANCELADA' | 'NO_ASISTIO';
export type ConversationStatus = 'ACTIVA' | 'FINALIZADA';
export type Sender = 'PACIENTE' | 'AGENTE';

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  userId: number;
  patientId: number;
  professionalId: number;
  role: Role;
}

export interface JwtPayload {
  sub: string;
  role: Role;
  iat?: number;
  exp?: number;
}

export interface User {
  id: number;
  fullName: string;
  email: string;
  identityDocument: string;
  role?: Role | { id: number; name: Role };
  verified: boolean;
  active: boolean;
  registrationDate: string;
  lastAccess?: string;
}

export interface Patient {
  id: number;
  user: User;
  birthDate?: string;
  phone?: string;
  medicalHistory?: {
    medical_history?: string;
    description?: string;
  };
}

export interface Specialty {
  id: number;
  name: string;
  description?: string;
}

export interface Professional {
  id: number;
  user: User;
  licenseNumber: string;
  specialty: Specialty;
  yearsExperience: number;
}

export interface Appointment {
  id: number;
  patient: Patient;
  professional: Professional;
  startTime: string;
  endTime: string;
  status: AppointmentStatus;
  preconsultationSummaryId?: number;
  postSummaryId?: number;
  cancellationReason?: string;
}

export interface Message {
  id: number;
  sender: Sender;
  content: string;
  sentAt: string;
}

export interface Conversation {
  id: number;
  patient: Patient;
  startDate?: string;
  endDate?: string;
  status: ConversationStatus;
  messages?: Message[];
}

export interface PreconsultationSummary {
  id: number;
  consultationReason: string;
  evolutionTime?: string;
  detailedSymptoms: string;
  relevantHistory?: string;
}

export interface AttentionSummary {
  id: number;
  diagnosis: string;
  recommendations: string;
  medications?: string;
  observations?: string;
  referral?: string;
}

export interface PostSummary {
  id: number;
  preconsultationSummary: PreconsultationSummary;
  attentionSummary: AttentionSummary;
  generatedAt: string;
}

export interface Notification {
  id: number;
  type: string;
  message: string;
  read: boolean;
  sentAt: string;
}

export interface ApiError {
  status?: number;
  message?: string;
  error?: string;
  path?: string;
  timestamp?: string;
}