import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { ServicioImpresion } from '../core/models';

@Component({
  selector: 'sigip-public-catalog',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './public-catalog.component.html',
  styleUrls: ['./public-catalog.component.css']
})
export class PublicCatalogComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  services: ServicioImpresion[] = [];
  loading = true;
  error = '';

  ngOnInit(): void {
    void this.loadServices();
  }

  requestService(service: ServicioImpresion): void {
    const queryParams = { servicio: service.idservicio };
    if (this.auth.isAuthenticated && this.auth.isClient) {
      void this.router.navigate(['/pedidos'], { queryParams });
    } else if (this.auth.isAuthenticated) {
      void this.router.navigate(['/dashboard']);
    } else {
      void this.router.navigate(['/registro'], { queryParams });
    }
  }

  async loadServices(): Promise<void> {
    this.loading = true;
    this.error = '';
    try {
      this.services = await firstValueFrom(this.api.services(true));
    } catch {
      this.error = 'No se pudo cargar el catálogo. Inténtalo nuevamente en unos minutos.';
    } finally {
      this.loading = false;
    }
  }
}
