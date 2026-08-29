import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class StorageService {
  private readonly prefix = 'telemed_';

  set<T>(key: string, value: T): void {
    localStorage.setItem(this.prefix + key, JSON.stringify(value));
  }

  get<T>(key: string): T | null {
    const value = localStorage.getItem(this.prefix + key);
    if (!value) return null;
    try {
      return JSON.parse(value) as T;
    } catch {
      return null;
    }
  }

  remove(key: string): void {
    localStorage.removeItem(this.prefix + key);
  }

  clearAuth(): void {
    ['access_token', 'refresh_token', 'user'].forEach((k) => this.remove(k));
  }
}