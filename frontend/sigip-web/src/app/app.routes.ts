import { Routes } from '@angular/router';
import { authGuard } from './core/auth.guard';
import { adminGuard } from './core/admin.guard';
import { LoginComponent } from './pages/login.component';
import { RegisterComponent } from './pages/register.component';
import { WorkspaceComponent } from './pages/workspace.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'registro', component: RegisterComponent },
  { path: 'dashboard', component: WorkspaceComponent, canActivate: [authGuard], data: { page: 'dashboard' } },
  { path: 'catalogo', component: WorkspaceComponent, canActivate: [authGuard], data: { page: 'catalogo' } },
  { path: 'cotizaciones', component: WorkspaceComponent, canActivate: [authGuard], data: { page: 'cotizaciones' } },
  { path: 'pagos', component: WorkspaceComponent, canActivate: [authGuard], data: { page: 'pagos' } }
  { path: 'pedidos', component: WorkspaceComponent, canActivate: [authGuard], data: { page: 'pedidos' } },
  { path: 'disenos', component: WorkspaceComponent, canActivate: [authGuard], data: { page: 'disenos' } },
  { path: 'notificaciones', component: WorkspaceComponent, canActivate: [authGuard], data: { page: 'notificaciones' } },
  { path: 'categorias', component: WorkspaceComponent, canActivate: [authGuard, adminGuard], data: { page: 'categorias' } },
  { path: 'servicios', component: WorkspaceComponent, canActivate: [authGuard, adminGuard], data: { page: 'servicios' } },
  { path: 'produccion', component: WorkspaceComponent, canActivate: [authGuard, adminGuard], data: { page: 'produccion' } },
  { path: 'entregas', component: WorkspaceComponent, canActivate: [authGuard, adminGuard], data: { page: 'entregas' } },
  { path: 'usuarios', component: WorkspaceComponent, canActivate: [authGuard, adminGuard], data: { page: 'usuarios' } }
  { path: 'inventario', component: WorkspaceComponent, canActivate: [authGuard, adminGuard], data: { page: 'inventario' } },
  { path: 'reportes', component: WorkspaceComponent, canActivate: [authGuard, adminGuard], data: { page: 'reportes' } },
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: '**', redirectTo: 'dashboard' }
];
