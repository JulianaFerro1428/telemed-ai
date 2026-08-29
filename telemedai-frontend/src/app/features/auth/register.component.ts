import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';

import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import {
  Router,
  RouterLink
} from '@angular/router';

import { IonicModule } from '@ionic/angular';

import { addIcons } from 'ionicons';

import {
  eyeOutline,
  eyeOffOutline
} from 'ionicons/icons';

import { AuthService } from '../../core/services/auth.service';


@Component({
  selector: 'app-register',

  standalone: true,

  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    IonicModule
  ],

  templateUrl: './register.component.html',

  styleUrl: './register.component.scss'
})
export class RegisterComponent {

  private readonly fb = inject(FormBuilder);

  private readonly auth = inject(AuthService);

  private readonly router = inject(Router);


  loading = false;

  showPassword = false;

 


  form = this.fb.nonNullable.group({

    fullName: [
      '',
      [
        Validators.required,
        Validators.minLength(3)
      ]
    ],

    email: [
      '',
      [
        Validators.required,
        Validators.email
      ]
    ],

    identityDocument: [
      '',
      [
        Validators.required
      ]
    ],

    password: [
      '',
      [
        Validators.required,
        Validators.minLength(8)
      ]
    ],


    phone: [
      '',
      [
        Validators.pattern(/^[0-9+\-\s]{7,15}$/)
      ]
    ],

    birthDate: [
      '',
      [
        Validators.pattern(/^\d{4}-\d{2}-\d{2}$/)
      ]
    ]

  });


  constructor() {

    addIcons({
      'eye-outline': eyeOutline,
      'eye-off-outline': eyeOffOutline
    });

  }


submit(): void {

  if (this.form.invalid) {
    this.form.markAllAsTouched();
    return;
  }

  const v = this.form.getRawValue();

  this.loading = true;

  this.auth.register({
    fullName: v.fullName,
    email: v.email,
    identityDocument: v.identityDocument,
    password: v.password,
    phone: v.phone,
    birthDate: v.birthDate
  }).subscribe({

    next: () => {
      this.loading = false;
      void this.router.navigate(['/dashboard']);
    },

    error: () => {
      this.loading = false;
    }

  });
}
}