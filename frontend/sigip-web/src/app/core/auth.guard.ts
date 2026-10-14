import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = route => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isAuthenticated) return true;

  const internalPages = [
    'categorias',
    'servicios',
    'produccion',
    'entregas',
    'usuarios',
    'inventario',
    'reportes'
  ];
  const page = String(route.data['page'] ?? '');
  const loginPath = internalPages.includes(page) ? '/login-interno' : '/login';

  return router.createUrlTree([loginPath]);
};
