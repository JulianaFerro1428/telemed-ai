import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { IonicModule } from '@ionic/angular';
import { addIcons } from 'ionicons';
import {
  searchOutline,
  notificationsOutline,
  personOutline,
  sendOutline,
  attachOutline,
  informationCircleOutline,
  sparklesOutline
} from 'ionicons/icons';
import { ShellComponent } from '../../shared/components/shell/shell.component';

@Component({
  selector: 'app-preconsulta',
  standalone: true,
  imports: [
    CommonModule,
    IonicModule,
    RouterLink,
    ShellComponent
  ],
  templateUrl: './preconsulta.component.html',
  styleUrl: './preconsulta.component.scss'
})
export class PreconsultaComponent {
  constructor() {
    addIcons({
      'search-outline': searchOutline,
      'notifications-outline': notificationsOutline,
      'person-outline': personOutline,
      'send-outline': sendOutline,
      'attach-outline': attachOutline,
      'information-circle-outline': informationCircleOutline,
      'sparkles-outline': sparklesOutline
    });
  }
}