import { Component, inject, signal } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { ButtonModule } from "primeng/button";
import { InputTextModule } from "primeng/inputtext";
import { InputTextareaModule } from "primeng/inputtextarea";

export const CONTACT_SUCCESS = "Demande de contact envoyée avec succès";

@Component({
  selector: "app-contact",
  standalone: true,
  imports: [ReactiveFormsModule, ButtonModule, InputTextModule, InputTextareaModule],
  template: `
    <h1 class="text-center">Contact</h1>
    <form [formGroup]="form" (ngSubmit)="send()" class="flex flex-column gap-3 m-auto" style="max-width: 32rem">
      <input pInputText type="email" formControlName="email" placeholder="Email" />
      @if (form.controls.email.touched && form.controls.email.invalid) {
        <small class="text-red-500">Un email valide est obligatoire</small>
      }
      <textarea pInputTextarea rows="6" formControlName="message" placeholder="Message"></textarea>
      <small [class.text-red-500]="form.controls.message.invalid && form.controls.message.dirty">
        {{ form.controls.message.value.length }}/299 — le message est obligatoire (moins de 300 caractères)
      </small>
      <p-button type="submit" label="Envoyer" [disabled]="form.invalid" />
      @if (sent()) { <div class="text-green-600 font-medium" role="status">{{ successMessage }}</div> }
    </form>
  `,
})
export class ContactComponent {
  private readonly fb = inject(FormBuilder);
  public readonly successMessage = CONTACT_SUCCESS;
  public readonly sent = signal(false);

  public readonly form = this.fb.nonNullable.group({
    email: ["", [Validators.required, Validators.email]],
    message: ["", [Validators.required, Validators.maxLength(299)]],
  });

  send() {
    if (this.form.invalid) return;
    // No contact endpoint in the spec: submission is simulated.
    this.sent.set(true);
    this.form.reset();
  }
}
