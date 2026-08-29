import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { ProfessionalService } from '../../core/services/professional.service';
import { Professional } from '../../core/models';
import { ShellComponent } from '../../shared/components/shell/shell.component';
import { LoadingComponent } from '../../shared/components/loading/loading.component';

@Component({
  selector: 'app-admin-professionals',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    IonicModule,
    ShellComponent,
    LoadingComponent
  ],
  templateUrl: './professionals.component.html',
  styleUrl: './professionals.component.scss'
})
export class ProfessionalsComponent {
  private readonly service = inject(ProfessionalService);
  private readonly fb = inject(FormBuilder);

  loading = true;
  saving = false;
  items: Professional[] = [];
  showForm = false;

  form = this.fb.nonNullable.group({
    fullName: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    identityDocument: ['', [Validators.required]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    licenseNumber: ['', [Validators.required]],
    specialtyId: [1, [Validators.required, Validators.min(1)]],
    yearsExperience: [0, [Validators.min(0)]]
  });

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.service.list().subscribe({
      next: (p) => {
        this.items = p;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  save(): void {
    if (this.form.invalid) return;

    this.saving = true;
    this.service.create(this.form.getRawValue()).subscribe({
      next: (p) => {
        this.items = [...this.items, p];
        this.form.reset({
          specialtyId: 1,
          yearsExperience: 0,
          fullName: '',
          email: '',
          identityDocument: '',
          password: '',
          licenseNumber: ''
        });
        this.saving = false;
        this.showForm = false;
      },
      error: () => {
        this.saving = false;
      }
    });
  }
}