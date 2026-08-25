import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/** Redirects the bare "/" route to the correct dashboard for the logged-in role. */
export const homeGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn()) {
    return router.createUrlTree(['/login']);
  }

  const isAdmin = authService.isAdmin();
  return router.createUrlTree([isAdmin ? '/admin/dashboard' : '/user/dashboard']);
};
