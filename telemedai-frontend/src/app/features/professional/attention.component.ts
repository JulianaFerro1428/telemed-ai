import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { AppointmentService } from '../../core/services/appointment.service';
import { SummaryService } from '../../core/services/summary.service';
import { AuthService } from '../../core/services/auth.service';
import { Appointment, AttentionSummary, PreconsultationSummary, PostSummary } from '../../core/models';
import { ShellComponent } from '../../shared/components/shell/shell.component';
import { LoadingComponent } from '../../shared/components/loading/loading.component';

@Component({
  selector: 'app-attention',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    IonicModule,
    ShellComponent,
    LoadingComponent
  ],
  templateUrl: './attention.component.html',
  styleUrl: './attention.component.scss'
})
export class AttentionComponent {
  private readonly fb = inject(FormBuilder);
  private readonly appointments = inject(AppointmentService);
  private readonly summaries = inject(SummaryService);
  private readonly auth = inject(AuthService);

  loading = true;
  saving = false;
  items: Appointment[] = [];
  selected: Appointment | null = null;
  pre: PreconsultationSummary | null = null;
  attention: AttentionSummary | null = null;
  post: PostSummary | null = null;

  preForm = this.fb.nonNullable.group({
    consultationReason: ['', [Validators.required]],
    evolutionTime: [''],
    detailedSymptoms: ['', [Validators.required]],
    relevantHistory: ['']
  });

  attentionForm = this.fb.nonNullable.group({
    diagnosis: ['', [Validators.required]],
    recommendations: ['', [Validators.required]],
    medications: [''],
    observations: [''],
    referral: ['']
  });

  ngOnInit(): void {
    const id = this.auth.getProfileId(); // ✅ Cambio

    if (!id) {
      this.loading = false;
      return;
    }

    this.appointments.byProfessional(id).subscribe({
      next: (a) => {
        this.items = a.filter(
          (x) =>
            x.status === 'CONFIRMADA' ||
            x.status === 'REPROGRAMADA' ||
            x.status === 'COMPLETADA'
        );
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  select(id: number): void {
    this.selected = this.items.find((x) => x.id === Number(id)) || null;
    this.pre = null;
    this.attention = null;
    this.post = null;

    if (this.selected?.preconsultationSummaryId) {
      this.summaries.getPre(this.selected.preconsultationSummaryId).subscribe({
        next: (p) => {
          this.pre = p;
        }
      });
    }
  }

  createPre(): void {
    if (!this.selected || this.preForm.invalid) return;

    this.saving = true;
    this.summaries
      .createPre({
        appointmentId: this.selected.id,
        ...this.preForm.getRawValue()
      })
      .subscribe({
        next: (p) => {
          this.pre = p;
          this.saving = false;
        },
        error: () => {
          this.saving = false;
        }
      });
  }

  createAttention(): void {
    if (!this.selected || this.attentionForm.invalid) return;

    this.saving = true;
    this.summaries
      .createAttention({
        appointmentId: this.selected.id,
        ...this.attentionForm.getRawValue()
      })
      .subscribe({
        next: (a) => {
          this.attention = a;
          this.saving = false;
        },
        error: () => {
          this.saving = false;
        }
      });
  }

  createPost(): void {
    if (!this.selected || !this.pre || !this.attention) return;

    this.saving = true;
    this.summaries
      .createPost({
        appointmentId: this.selected.id,
        preconsultationSummaryId: this.pre.id,
        attentionSummaryId: this.attention.id
      })
      .subscribe({
        next: (p) => {
          this.post = p;
          this.saving = false;
        },
        error: () => {
          this.saving = false;
        }
      });
  }
}