import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, JwtPayload, Role, User } from '../models';
import { StorageService } from './storage.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly storage = inject(StorageService);
  private readonly base = environment.apiUrl;
  private readonly userSubject = new BehaviorSubject<User | null>(this.storage.get<User>('user'));
  readonly user$ = this.userSubject.asObservable();

  register(payload: {
    fullName: string;
    email: string;
    identityDocument: string;
    password: string;
    phone?: string;
    birthDate?: string;
  }): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.base}/auth/register`, payload)
      .pipe(tap((r) => this.saveTokens(r)));
  }

  login(payload: { email: string; password: string }): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.base}/auth/login`, payload)
      .pipe(tap((r) => this.saveTokens(r)));
  }

  refresh(refreshToken: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.base}/auth/refresh`, { refreshToken })
      .pipe(tap((r) => this.saveTokens(r)));
  }

  requestPasswordRecovery(email: string): Observable<void> {
    return this.http.post<void>(`${this.base}/auth/password-recovery`, { email });
  }

  resetPassword(token: string, newPassword: string): Observable<void> {
    return this.http.post<void>(`${this.base}/auth/password-reset`, { token, newPassword });
  }

  saveTokens(response: AuthResponse): void {
    // Guardar tokens
    this.storage.set('access_token', response.accessToken);
    this.storage.set('refresh_token', response.refreshToken);

    // Guardar IDs y rol (siempre)
    this.storage.set('userId', response.userId ?? null);
    this.storage.set('patientId', response.patientId ?? null);
    this.storage.set('professionalId', response.professionalId ?? null);
    if (response.role) {
      this.storage.set('role', response.role);
    }

    // Actualizar usuario en el subject
    const payload = this.decodeToken(response.accessToken);
    const previous = this.userSubject.value;
    if (payload) {
      const user: User = previous ?? {
        id: response.userId ?? 0,
        fullName: payload.sub,
        email: payload.sub,
        identityDocument: '',
        role: response.role ?? payload.role,
        verified: false,
        active: true,
        registrationDate: ''
      };
      user.id = response.userId ?? user.id;
      user.role = response.role ?? payload.role;
      user.email = payload.sub;
      this.storage.set('user', user);
      this.userSubject.next(user);
    }
  }

  // ✅ Obtener professionalId
  getProfessionalId(): number | null {
    return this.storage.get<number>('professionalId');
  }

  // ✅ Obtener patientId
  getPatientId(): number | null {
    return this.storage.get<number>('patientId');
  }

  // ✅ Obtener userId
  getUserId(): number | null {
    return this.storage.get<number>('userId');
  }

  // ✅ Obtener rol desde el storage (más confiable que userSubject)
  getRole(): Role | null {
    // Primero intentar desde el storage (más seguro)
    const storedRole = this.storage.get<string>('role');
    if (storedRole) return storedRole as Role;

    // Fallback: desde userSubject
    const user = this.userSubject.value;
    if (user && typeof user.role === 'string') return user.role;
    if (user && user.role && typeof user.role === 'object' && 'name' in user.role) {
      return user.role.name as Role;
    }
    return null;
  }

  // ✅ NUEVO: getProfileId() usa el rol del storage
  getProfileId(): number | null {
    const role = this.getRole();

    // Para profesionales, usar professionalId (prioridad) o userId como fallback
    if (role === 'PROFESIONAL') {
      const profId = this.getProfessionalId();
      if (profId) return profId;
      return this.getUserId();
    }

    // Para PACIENTE o ADMIN: priorizar patientId, luego userId
    const patientId = this.getPatientId();
    if (patientId) return patientId;
    return this.getUserId();
  }

  getStoredRole(): string | null {
    return this.storage.get<string>('role');
  }

  setCurrentUser(user: User): void {
    this.storage.set('user', user);
    this.userSubject.next(user);
  }

  getAccessToken(): string | null {
    return this.storage.get<string>('access_token');
  }

  getRefreshToken(): string | null {
    return this.storage.get<string>('refresh_token');
  }

  getCurrentUser(): User | null {
    return this.userSubject.value;
  }

  isAuthenticated(): boolean {
    return !!this.getAccessToken();
  }

  logout(): void {
    this.storage.clearAuth();
    this.storage.remove('userId');
    this.storage.remove('patientId');
    this.storage.remove('professionalId');
    this.storage.remove('role');
    this.storage.remove('profile_id');
    this.userSubject.next(null);
  }

  decodeToken(token: string): JwtPayload | null {
    try {
      const part = token.split('.')[1];
      const normalized = part.replace(/-/g, '+').replace(/_/g, '/');
      const json = decodeURIComponent(
        atob(normalized)
          .split('')
          .map((c) => `%${('00' + c.charCodeAt(0).toString(16)).slice(-2)}`)
          .join('')
      );
      return JSON.parse(json) as JwtPayload;
    } catch {
      return null;
    }
  }
}