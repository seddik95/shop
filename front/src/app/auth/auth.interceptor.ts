import { HttpErrorResponse, HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";
import { catchError, throwError } from "rxjs";
import { AuthService } from "./auth.service";

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.token();
  const isApi = req.url.startsWith("/api/");
  const isProductSave =
    (req.method === "POST" || req.method === "PATCH") &&
    /^\/api\/products(?:\/|$)/.test(req.url);
  const authed = isApi && token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;
  return next(authed).pipe(
    catchError((err: HttpErrorResponse) => {
      // expired/invalid token on a protected call => back to login
      if (err.status === 401 && isApi && !req.url.endsWith("/token") && !isProductSave) auth.logout();
      return throwError(() => err);
    }),
  );
};
