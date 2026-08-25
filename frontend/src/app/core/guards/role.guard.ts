import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/** Restricts a route to ADMIN users only. Route data: { roles: ['ADMIN'] } */
export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const allowedRoles = (route.data?.['roles'] as string[]) ?? [];
  const currentRole = authService.currentUser()?.role;

  if (currentRole && allowedRoles.includes(currentRole)) {
    return true;
  }

  router.navigate(['/']);
  return false;
};
