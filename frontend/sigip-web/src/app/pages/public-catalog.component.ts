import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { ServicioImpresion } from '../core/models';

@Component({
  selector: 'sigip-public-catalog',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
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
  searchTerm = '';
  categoryFilter = 'Todos';

  get categories(): string[] {
    return [...new Set(this.services.map(s => s.categoria?.trim()).filter((c): c is string => !!c))].sort((a,b) => a.localeCompare(b, 'es'));
  }

  get filteredServices(): ServicioImpresion[] {
    const query = this.searchTerm.trim().toLocaleLowerCase('es');
    return this.services.filter(s => {
      const matchesCategory = this.categoryFilter === 'Todos' || (s.categoria || '') === this.categoryFilter;
      const text = `${s.nombre} ${s.categoria || ''} ${s.descripcion || ''}`.toLocaleLowerCase('es');
      return matchesCategory && (!query || text.includes(query));
    });
  }

  imageFor(service: ServicioImpresion): string {
    const text = `${service.nombre} ${service.categoria || ''} ${service.descripcion || ''}`
      .normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase();
    if (/tarjeta|presentacion|business card/.test(text)) return '/assets/images/servicios/tarjetas-presentacion.svg';
    if (/volante|folleto|brochure|triptico|diptico|flyer/.test(text)) return '/assets/images/servicios/folletos.svg';
    if (/banner|gigantografia|lona|pendon|roll.?up|cartel|vinil/.test(text)) return '/assets/images/servicios/banner.svg';
    if (/sticker|etiqueta|calcomania|adhesivo/.test(text)) return '/assets/images/servicios/etiquetas-stickers.svg';
    if (/taza|polo|camiseta|sublimacion|promocional/.test(text)) return '/assets/images/servicios/promocionales.svg';
    if (/libro|revista|catalogo|cuaderno|agenda|empastado/.test(text)) return '/assets/images/servicios/libros-revistas.svg';
    return '/assets/images/servicios/impresion-general.svg';
  }

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
