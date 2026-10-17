import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

/** Solo usuarios con sesión iniciada. */
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  return auth.autenticado() ? true : inject(Router).createUrlTree(['/login']);
};

/** Solo el rol ADMIN (el backend también lo valida con @PreAuthorize). */
export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  return auth.esAdmin() ? true : inject(Router).createUrlTree(['/inicio']);
};

/** Pantallas públicas (login, registro): si ya hay sesión, se va al inicio. */
export const invitadoGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  return auth.autenticado() ? inject(Router).createUrlTree(['/inicio']) : true;
};


/** Crea un guard para las áreas internas. ADMIN puede acceder a todos los módulos. */
export const rolesGuard = (rolesPermitidos: string[]): CanActivateFn => () => {
  const auth = inject(AuthService);
  const autorizado = auth.tieneRol('ADMIN') || rolesPermitidos.some((rol) =>
    auth.usuario()?.roles.includes(rol as import('./models').Rol),
  );
  return autorizado ? true : inject(Router).createUrlTree(['/inicio']);
};
