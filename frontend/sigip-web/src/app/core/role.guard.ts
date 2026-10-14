import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const roleGuard: CanActivateFn = route => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const allowedRoles = route.data['roles'] as string[] | undefined;

  if (!auth.isAuthenticated) return router.createUrlTree(['/login']);
  if (auth.isAdmin || !allowedRoles?.length || allowedRoles.some(role => auth.hasRole(role))) return true;
  return router.createUrlTree(['/dashboard']);
};
