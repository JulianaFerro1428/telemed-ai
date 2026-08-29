import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router } from '@angular/router';
import { Role } from '../models';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const roles = route.data['roles'] as Role[] | undefined;
  const role = auth.getRole();

  if (roles?.includes(role as Role)) {
    return true;
  }

  // Redirigir según el rol si no tiene permiso
  if (role === 'PROFESIONAL') {
    return router.createUrlTree(['/professional/schedule']);
  }
  if (role === 'ADMIN') {
    return router.createUrlTree(['/admin/professionals']);
  }
  // Para PACIENTE o cualquier otro, ir al dashboard
  return router.createUrlTree(['/dashboard']);
};