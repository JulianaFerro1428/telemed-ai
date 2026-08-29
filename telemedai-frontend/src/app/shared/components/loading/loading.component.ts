import { Component, Input } from '@angular/core';
import { IonicModule } from '@ionic/angular';

@Component({
  selector: 'app-loading',
  standalone: true,
  imports: [IonicModule],
  template: `
    <div class="loading">
      <ion-spinner name="crescent"></ion-spinner>
      <span>{{ message }}</span>
    </div>
  `,
  styles: [
    `
      .loading {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 10px;
        padding: 35px;
        color: #68788f;
      }

      .loading ion-spinner {
        color: #2c7be5;
      }
    `
  ]
})
export class LoadingComponent {
  @Input() message = 'Cargando...';
}