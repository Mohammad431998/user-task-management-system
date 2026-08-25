import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

/** Attaches the JWT bearer token to every outgoing API request and
 * redirects to the login page on 401 (session expired / unauthorized). */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const token = authService.getToken();
  const authedReq = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authedReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        authService.currentUser.set(null);
        localStorage.removeItem('utms_token');
        localStorage.removeItem('utms_user');
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};
