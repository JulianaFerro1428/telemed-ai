import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

const routes: Routes = [
  {path: '', pathMatch: 'full', loadComponent: () => import('./features/dashboard/landing.component').then(m => m.LandingComponent) },
  { path: 'login', loadComponent: () => import('./features/auth/login.component').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./features/auth/register.component').then(m => m.RegisterComponent) },
  { path: 'forgot-password', loadComponent: () => import('./features/auth/forgot-password.component').then(m => m.ForgotPasswordComponent) },
  { path: 'reset-password', loadComponent: () => import('./features/auth/reset-password.component').then(m => m.ResetPasswordComponent) },
  { path: 'dashboard', canActivate: [authGuard, roleGuard], data: { roles: ['PACIENTE', 'ADMIN'] }, loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent) },
  { path: 'appointments', canActivate: [authGuard, roleGuard], data: { roles: ['PACIENTE', 'ADMIN'] }, loadComponent: () => import('./features/appointment/appointments.component').then(m => m.AppointmentsComponent) },
  { path: 'appointments/book', canActivate: [authGuard, roleGuard], data: { roles: ['PACIENTE', 'ADMIN'] }, loadComponent: () => import('./features/appointment/book-appointment.component').then(m => m.BookAppointmentComponent) },
  { path: 'appointments/reschedule/:id', canActivate: [authGuard, roleGuard], data: { roles: ['PACIENTE', 'ADMIN'] }, loadComponent: () => import('./features/appointment/book-appointment.component').then(m => m.BookAppointmentComponent)},
  { path: 'profile', canActivate: [authGuard, roleGuard], data: { roles: ['PACIENTE', 'ADMIN'] }, loadComponent: () => import('./features/patient/profile.component').then(m => m.ProfileComponent) },
  { path: 'history', canActivate: [authGuard, roleGuard], data: { roles: ['PACIENTE', 'ADMIN'] }, loadComponent: () => import('./features/patient/history.component').then(m => m.HistoryComponent) },
  { path: 'preconsulta', canActivate: [authGuard, roleGuard], data: { roles: ['PACIENTE', 'ADMIN'] }, loadComponent: () => import('./features/agent/preconsulta.component').then(m => m.PreconsultaComponent) },
  { path: 'notifications', canActivate: [authGuard], loadComponent: () => import('./features/notification/notifications.component').then(m => m.NotificationsComponent) },
  { path: 'professional/schedule', canActivate: [authGuard, roleGuard], data: { roles: ['PROFESIONAL', 'ADMIN'] }, loadComponent: () => import('./features/professional/schedule.component').then(m => m.ScheduleComponent) },
  { path: 'professional/appointments', canActivate: [authGuard, roleGuard], data: { roles: ['PROFESIONAL', 'ADMIN'] }, loadComponent: () => import('./features/professional/professional-appointments.component').then(m => m.ProfessionalAppointmentsComponent) },
  { path: 'professional/attention', canActivate: [authGuard, roleGuard], data: { roles: ['PROFESIONAL', 'ADMIN'] }, loadComponent: () => import('./features/professional/attention.component').then(m => m.AttentionComponent) },
  { path: 'admin/users', canActivate: [authGuard, roleGuard], data: { roles: ['ADMIN'] }, loadComponent: () => import('./features/admin/users.component').then(m => m.UsersComponent) },
  { path: 'admin/professionals', canActivate: [authGuard, roleGuard], data: { roles: ['ADMIN'] }, loadComponent: () => import('./features/admin/professionals.component').then(m => m.ProfessionalsComponent) },
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { scrollPositionRestoration: 'enabled' })],
  exports: [RouterModule]
})
export class AppRoutingModule {}