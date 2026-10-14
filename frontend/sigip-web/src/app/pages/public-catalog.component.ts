import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { ApiService } from '../core/api.service';
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
  services: ServicioImpresion[] = [];
  loading = true;
  error = '';

  ngOnInit(): void {
    void this.loadServices();
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
