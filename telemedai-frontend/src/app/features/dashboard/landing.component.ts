import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { IonicModule } from '@ionic/angular';

import { addIcons } from 'ionicons';
import {
  arrowForwardOutline,
  timeOutline,
  calendarOutline,
  documentTextOutline,
  personAddOutline,
  folderOutline,
  shieldCheckmarkOutline,
  warningOutline
} from 'ionicons/icons';

import { HeaderComponent } from '../../shared/components/header/header.component';
import { FooterComponent } from '../../shared/components/footer/footer.component';

@Component({
  selector: 'app-landing',
  standalone: true,

  imports: [
    CommonModule,
    IonicModule,
    RouterLink,
    HeaderComponent,
    FooterComponent
  ],

  templateUrl: './landing.component.html',
  styleUrl: './landing.component.scss'
})
export class LandingComponent {

  constructor() {

    addIcons({
      'arrow-forward-outline': arrowForwardOutline,
      'time-outline': timeOutline,
      'calendar-outline': calendarOutline,
      'document-text-outline': documentTextOutline,
      'person-add-outline': personAddOutline,
      'folder-outline': folderOutline,
      'shield-checkmark-outline': shieldCheckmarkOutline,
      'warning-outline': warningOutline
    });

  }

}