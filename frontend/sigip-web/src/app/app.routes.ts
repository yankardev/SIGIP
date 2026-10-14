import { Routes } from '@angular/router';
import { authGuard } from './core/auth.guard';
import { adminGuard } from './core/admin.guard';
import { roleGuard } from './core/role.guard';
import { LandingComponent } from './pages/landing.component';
import { PublicCatalogComponent } from './pages/public-catalog.component';
import { LoginComponent } from './pages/login.component';
import { RegisterComponent } from './pages/register.component';
import { WorkspaceComponent } from './pages/workspace.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', component: LandingComponent },
  { path: 'inicio', component: LandingComponent },
  { path: 'catalogo-publico', component: PublicCatalogComponent },
  { path: 'login', component: LoginComponent, data: { internal: false } },
  { path: 'login-interno', component: LoginComponent, data: { internal: true } },
  { path: 'registro', component: RegisterComponent },
  { path: 'dashboard', component: WorkspaceComponent, canActivate: [authGuard], data: { page: 'dashboard' } },
  { path: 'catalogo', component: WorkspaceComponent, canActivate: [authGuard, roleGuard], data: { page: 'catalogo', roles: ['CLIENTE', 'VENTAS'] } },
  { path: 'cotizaciones', component: WorkspaceComponent, canActivate: [authGuard, roleGuard], data: { page: 'cotizaciones', roles: ['CLIENTE', 'VENTAS'] } },
  { path: 'pedidos', component: WorkspaceComponent, canActivate: [authGuard, roleGuard], data: { page: 'pedidos', roles: ['CLIENTE', 'VENTAS', 'CAJA'] } },
  { path: 'pagos', component: WorkspaceComponent, canActivate: [authGuard, roleGuard], data: { page: 'pagos', roles: ['CLIENTE', 'CAJA'] } },
  { path: 'disenos', component: WorkspaceComponent, canActivate: [authGuard, roleGuard], data: { page: 'disenos', roles: ['CLIENTE', 'DISENO'] } },
  { path: 'notificaciones', component: WorkspaceComponent, canActivate: [authGuard], data: { page: 'notificaciones' } },
  { path: 'categorias', component: WorkspaceComponent, canActivate: [authGuard, adminGuard], data: { page: 'categorias' } },
  { path: 'servicios', component: WorkspaceComponent, canActivate: [authGuard, adminGuard], data: { page: 'servicios' } },
  { path: 'produccion', component: WorkspaceComponent, canActivate: [authGuard, roleGuard], data: { page: 'produccion', roles: ['PRODUCCION'] } },
  { path: 'entregas', component: WorkspaceComponent, canActivate: [authGuard, roleGuard], data: { page: 'entregas', roles: ['PRODUCCION', 'VENTAS'] } },
  { path: 'seguridad', component: WorkspaceComponent, canActivate: [authGuard, adminGuard], data: { page: 'usuarios' } },
  { path: 'usuarios', pathMatch: 'full', redirectTo: 'seguridad' },
  { path: 'inventario', component: WorkspaceComponent, canActivate: [authGuard, roleGuard], data: { page: 'inventario', roles: ['ALMACEN'] } },
  { path: 'reportes', component: WorkspaceComponent, canActivate: [authGuard, roleGuard], data: { page: 'reportes', roles: ['VENTAS', 'CAJA'] } },
  { path: '**', redirectTo: 'inicio' }
];
