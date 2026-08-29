import { Component, Input, Output, EventEmitter } from '@angular/core';
import { IonicModule } from '@ionic/angular';

@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [IonicModule],
  template: `
    <ion-modal [isOpen]="open" (didDismiss)="closed.emit()">
      <ng-content></ng-content>
    </ion-modal>
  `
})
export class ModalComponent {
  @Input() open = false;
  @Output() closed = new EventEmitter<void>();
}