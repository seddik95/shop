import { Component, inject, signal } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { ButtonModule } from "primeng/button";
import { InputTextModule } from "primeng/inputtext";
import { AuthService } from "./auth.service";

@Component({
  selector: "app-login",
  standalone: true,
  imports: [ReactiveFormsModule, ButtonModule, InputTextModule],
  styleUrls: ["./login.component.scss"],
  template: `
    <div class="auth-page">
      <section class="auth-card" aria-labelledby="auth-title">
        <div class="auth-intro">
          <div class="eyebrow"><span></span> ALTEN SHOP</div>
          <h1 id="auth-title">{{ registerMode() ? "Rejoignez-nous" : "Ravi de vous revoir" }}</h1>
          <p>
            {{ registerMode()
              ? "Créez votre compte pour profiter de votre espace personnel."
              : "Connectez-vous pour retrouver votre espace et vos produits préférés." }}
          </p>
          <div class="intro-art" aria-hidden="true"></div>
          <div class="intro-note"><i class="pi pi-shield"></i> Une expérience pensée pour vous</div>
        </div>

        <div class="auth-form-panel">
          <div class="form-heading">
            <span class="form-kicker">{{ registerMode() ? "NOUVEAU COMPTE" : "VOTRE ESPACE" }}</span>
            <h2>{{ registerMode() ? "Créer un compte" : "Connexion" }}</h2>
            <p>{{ registerMode() ? "Quelques informations pour commencer." : "Entrez vos identifiants pour continuer." }}</p>
          </div>

          <form [formGroup]="form" (ngSubmit)="submit()" class="auth-form">
            @if (registerMode()) {
              <label for="username">Nom d'utilisateur</label>
              <input id="username" pInputText formControlName="username" placeholder="Votre nom d'utilisateur" autocomplete="username" />
              <label for="firstname">Prénom</label>
              <input id="firstname" pInputText formControlName="firstname" placeholder="Votre prénom" autocomplete="given-name" />
            }
            <label for="email">Adresse e-mail</label>
            <span class="input-with-icon">
              <i class="pi pi-envelope" aria-hidden="true"></i>
              <input id="email" pInputText type="email" formControlName="email" placeholder="nom@exemple.fr" autocomplete="email" />
            </span>
            <label for="password">Mot de passe</label>
            <span class="input-with-icon">
              <i class="pi pi-lock" aria-hidden="true"></i>
              <input id="password" pInputText type="password" formControlName="password" placeholder="6 caractères minimum" autocomplete="current-password" />
            </span>
            @if (error()) { <small class="form-error" role="alert"><i class="pi pi-exclamation-circle"></i> {{ error() }}</small> }
            <p-button type="submit" [label]="submitLabel()" icon="pi pi-arrow-right" iconPos="right" [disabled]="form.invalid" styleClass="submit-button" />
          </form>

          <div class="form-switch">
            <span>{{ registerMode() ? "Vous avez déjà un compte ?" : "Pas encore de compte ?" }}</span>
            <button type="button" (click)="toggle()">{{ switchLabel() }}</button>
          </div>
        </div>
      </section>
      <p class="page-caption">ALTEN SHOP <span>·</span> Votre sélection, en toute simplicité</p>
    </div>
  `,
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  public readonly registerMode = signal(false);
  public readonly error = signal("");
  public readonly submitLabel = () => (this.registerMode() ? "S'inscrire" : "Se connecter");
  public readonly switchLabel = () => (this.registerMode() ? "J'ai déjà un compte" : "Créer un compte");

  public readonly form = this.fb.nonNullable.group({
    username: [""],
    firstname: [""],
    email: ["", [Validators.required, Validators.email]],
    password: ["", [Validators.required, Validators.minLength(6)]],
  });

  toggle() {
    const reg = !this.registerMode();
    this.registerMode.set(reg);
    for (const c of ["username", "firstname"] as const) {
      this.form.controls[c].setValidators(reg ? [Validators.required] : []);
      this.form.controls[c].updateValueAndValidity();
    }
    this.error.set("");
  }

  submit() {
    if (this.form.invalid) return;
    const { username, firstname, email, password } = this.form.getRawValue();
    const req = this.registerMode()
      ? this.auth.register(username, firstname, email, password)
      : this.auth.login(email, password);
    req.subscribe({
      next: () => this.router.navigate(["/products/list"]),
      error: (e) => this.error.set(e.status === 409 ? "Email déjà utilisé" : e.status === 401 ? "Identifiants invalides" : "Erreur, réessayez"),
    });
  }
}
