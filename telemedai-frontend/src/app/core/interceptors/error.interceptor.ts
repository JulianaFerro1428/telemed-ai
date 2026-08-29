import {
  HttpErrorResponse,
  HttpInterceptorFn
} from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { ToastController } from '@ionic/angular';
import { Observable, catchError, finalize, shareReplay, switchMap, throwError } from 'rxjs';
import { AuthResponse } from '../models';
import { AuthService } from '../services/auth.service';

let refreshRequest$: Observable<AuthResponse> | null = null;

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const toast = inject(ToastController);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      const isRefresh = req.url.includes('/auth/refresh');
      const refreshToken = auth.getRefreshToken();

      if (error.status === 401 && refreshToken && !isRefresh) {
        if (!refreshRequest$) {
          refreshRequest$ = auth.refresh(refreshToken).pipe(
            finalize(() => (refreshRequest$ = null)),
            shareReplay(1)
          );
        }
        return refreshRequest$.pipe(
          switchMap(() => {
            const token = auth.getAccessToken();
            return next(
              req.clone({
                setHeaders: {
                  Authorization: `Bearer ${token ?? ''}`
                }
              })
            );
          }),
          catchError((refreshError) => {
            auth.logout();
            void router.navigate(['/login']);
            return throwError(() => refreshError);
          })
        );
      }

      if (error.status === 401 && !isRefresh) {
        auth.logout();
        void router.navigate(['/login']);
      }

      const message = error.error?.message || friendlyError(error.status);
      void toast
        .create({
          message,
          duration: 3200,
          color: 'danger',
          position: 'top'
        })
        .then((t) => t.present());

      return throwError(() => error);
    })
  );
};

function friendlyError(status: number): string {
  if (status === 0) return 'No fue posible conectar con el servidor. Verifica que Spring Boot esté ejecutándose.';
  if (status === 400) return 'Revisa los datos enviados.';
  if (status === 403) return 'No tienes permisos para realizar esta acción.';
  if (status === 404) return 'No se encontró el recurso solicitado.';
  if (status >= 500) return 'El servidor presentó un error. Revisa la consola de Spring Boot.';
  return 'Ocurrió un error inesperado.';
}