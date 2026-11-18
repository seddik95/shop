import { HttpClient } from "@angular/common/http";
import { Injectable, computed, effect, inject, signal } from "@angular/core";
import { Router } from "@angular/router";
import { Observable, switchMap, tap } from "rxjs";

const TOKEN_KEY = "token";
const ADMIN_EMAIL = "admin@admin.com";

interface JwtPayload { sub?: string; exp?: number }

function decode(token: string | null): JwtPayload | null {
  if (!token) return null;
  try {
    const b64 = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
    return JSON.parse(atob(b64));
  } catch {
    return null;
  }
}

@Injectable({ providedIn: "root" })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly _token = signal<string | null>(localStorage.getItem(TOKEN_KEY));
  public readonly token = this._token.asReadonly();

  private readonly payload = computed(() => decode(this._token()));
  private readonly expirationMonitor = effect((onCleanup) => {
    const token = this._token();
    const exp = this.payload()?.exp;
    if (!token || !exp) return;

    const timeout = setTimeout(() => {
      if (this._token() === token) this.logout();
    }, Math.max(0, exp * 1000 - Date.now()));
    onCleanup(() => clearTimeout(timeout));
  });

  public readonly isLoggedIn = computed(() => {
    const exp = this.payload()?.exp;
    return !!this.payload() && (!exp || exp * 1000 > Date.now());
  });
  public readonly email = computed(() => this.payload()?.sub ?? null);
  // UI convenience only: the backend is the one enforcing the admin rule.
  public readonly isAdmin = computed(() => this.isLoggedIn() && this.email() === ADMIN_EMAIL);

  login(email: string, password: string): Observable<{ token: string }> {
    return this.http.post<{ token: string }>("/api/token", { email, password }).pipe(
      tap(({ token }) => {
        localStorage.setItem(TOKEN_KEY, token);
        this._token.set(token);
      }),
    );
  }

  register(username: string, firstname: string, email: string, password: string) {
    return this.http
      .post("/api/account", { username, firstname, email, password })
      .pipe(switchMap(() => this.login(email, password)));
  }

  logout(redirect = true) {
    localStorage.removeItem(TOKEN_KEY);
    this._token.set(null);
    if (redirect) this.router.navigate(["/login"]);
  }
}
