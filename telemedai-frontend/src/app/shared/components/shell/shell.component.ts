import { CommonModule } from '@angular/common';

import {
  Component,
  inject
} from '@angular/core';

import {
  Router,
  RouterLink,
  RouterLinkActive
} from '@angular/router';

import { IonicModule } from '@ionic/angular';

import { addIcons } from 'ionicons';

import {
  gridOutline,
  calendarOutline,
  addCircleOutline,
  sparklesOutline,
  documentTextOutline,
  personOutline,
  timeOutline,
  clipboardOutline,
  medkitOutline,
  peopleOutline,
  shieldCheckmarkOutline,
  notificationsOutline,
  logOutOutline,
  homeOutline
} from 'ionicons/icons';

import { AuthService } from '../../../core/services/auth.service';


@Component({
  selector: 'app-shell',

  standalone: true,

  imports: [
    CommonModule,
    IonicModule,
    RouterLink,
    RouterLinkActive
  ],

  templateUrl: './shell.component.html',

  styleUrls: ['./shell.component.scss']
})
export class ShellComponent {

  readonly auth = inject(AuthService);

  private readonly router = inject(Router);


  constructor() {

    addIcons({

      'grid-outline': gridOutline,

      'calendar-outline': calendarOutline,

      'add-circle-outline': addCircleOutline,

      'sparkles-outline': sparklesOutline,

      'document-text-outline': documentTextOutline,

      'person-outline': personOutline,

      'time-outline': timeOutline,

      'clipboard-outline': clipboardOutline,

      'medkit-outline': medkitOutline,

      'people-outline': peopleOutline,

      'shield-checkmark-outline': shieldCheckmarkOutline,

      'notifications-outline': notificationsOutline,

      'log-out-outline': logOutOutline,

      'home-outline': homeOutline

    });

  }


  get role() {

    return this.auth.getRole();

  }


  get user() {

    return this.auth.getCurrentUser();

  }


  logout(): void {

    this.auth.logout();

    void this.router.navigate(['/login']);

  }

}