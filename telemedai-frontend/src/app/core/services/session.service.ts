import { Injectable, inject } from '@angular/core';
import { AuthService } from './auth.service';
import { StorageService } from './storage.service';

@Injectable({
  providedIn: 'root'
})
export class SessionService {
  private readonly storage = inject(StorageService);
  private readonly auth = inject(AuthService);
  private readonly key = 'profile_id';

  // Ahora usa AuthService para obtener el patientId real
  get profileId(): number | null {
    return this.auth.getPatientId();
  }

  // Para profesionales, obtener userId
  get userId(): number | null {
    return this.auth.getUserId();
  }

  // Método para guardar (si se necesita manualmente)
  setProfileId(id: number): void {
    this.storage.set(this.key, id);
  }

  clear(): void {
    this.storage.remove(this.key);
  }

  get role() {
    return this.auth.getRole();
  }
}