import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { PatientService } from '../../core/services/patient.service';
import { AuthService } from '../../core/services/auth.service';
import { Patient } from '../../core/models';
import { ShellComponent } from '../../shared/components/shell/shell.component';
import { LoadingComponent } from '../../shared/components/loading/loading.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    IonicModule,
    ShellComponent,
    LoadingComponent
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent {
  private readonly service = inject(PatientService);
  private readonly auth = inject(AuthService);
  private readonly fb = inject(FormBuilder);

  patient: Patient | null = null;
  loading = true;
  saving = false;
  editing = false;

  form = this.fb.nonNullable.group({
    fullName: ['', [Validators.required]],
    phone: [''],
    birthDate: [''],
    medicalHistory: ['']
  });

  ngOnInit(): void {
    const id = this.auth.getProfileId(); // ✅ Cambio

    if (!id) {
      this.loading = false;
      return;
    }

    this.service.get(id).subscribe({
      next: (p) => {
        this.patient = p;
        this.patch(p);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  patch(p: Patient): void {
    this.form.patchValue({
      fullName: p.user?.fullName || '',
      phone: p.phone || '',
      birthDate: p.birthDate || '',
      medicalHistory: p.medicalHistory?.medical_history ?? p.medicalHistory?.description ?? ''
    });
  }

  save(): void {
    if (this.form.invalid || !this.patient) return;

    this.saving = true;
    this.service.update(this.patient.id, this.form.getRawValue()).subscribe({
      next: (p) => {
        this.patient = p;
        this.patch(p);
        this.saving = false;
        this.editing = false;
      },
      error: () => {
        this.saving = false;
      }
    });
  }
}