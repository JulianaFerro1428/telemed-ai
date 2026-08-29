import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { IonicModule } from '@ionic/angular';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, IonicModule],
  templateUrl: './reset-password.component.html',
  styleUrl: './auth.component.scss'
})
export class ResetPasswordComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  loading = false;

  form = this.fb.nonNullable.group({
    token: ['', [Validators.required]],
    newPassword: ['', [Validators.required, Validators.minLength(8)]]
  });

  submit(): void {
    if (this.form.invalid) return;

    this.loading = true;
    this.auth.resetPassword(
      this.form.getRawValue().token,
      this.form.getRawValue().newPassword
    ).subscribe({
      next: () => {
        this.loading = false;
        void this.router.navigate(['/login']);
      },
      error: () => {
        this.loading = false;
      }
    });
  }
}