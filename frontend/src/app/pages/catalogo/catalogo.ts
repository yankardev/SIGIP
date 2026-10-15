import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Categoria, Servicio } from '../../core/models';
import { CatalogoService } from '../../core/services/catalogo.service';
import { VentasService } from '../../core/services/ventas.service';
import { NotificadorService } from '../../core/notificador.service';
import { Modal } from '../../shared/modal';

/** Catálogo de servicios activos; desde aquí el cliente registra su pedido. */
@Component({
  selector: 'app-catalogo',
  imports: [CurrencyPipe, FormsModule, Modal],
  templateUrl: './catalogo.html',
})
export class Catalogo implements OnInit {
  private readonly catalogo = inject(CatalogoService);
  private readonly ventas = inject(VentasService);
  private readonly notificador = inject(NotificadorService);
  private readonly router = inject(Router);

  protected readonly cargando = signal(true);
  protected readonly categorias = signal<Categoria[]>([]);
  protected readonly servicios = signal<Servicio[]>([]);
  protected readonly categoriaSeleccionada = signal<number | null>(null);
  protected readonly busqueda = signal('');

  protected readonly visibles = computed(() => {
    const texto = this.busqueda().trim().toLowerCase();
    const cat = this.categoriaSeleccionada();
    return this.servicios().filter(
      (s) =>
        (cat === null || s.idcategoria === cat) &&
        (!texto || s.nombre.toLowerCase().includes(texto) || (s.descripcion ?? '').toLowerCase().includes(texto)),
    );
  });

  // Muestras locales para que el catálogo no dependa de servicios de imágenes externos.
  protected imagenServicio(servicio: Servicio): string {
    const texto = `${servicio.nombre} ${servicio.categoria} ${servicio.descripcion ?? ''}`
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLowerCase();

    if (/tarjeta|presentacion|business card/.test(texto)) return '/images/servicios/tarjetas-presentacion.svg';
    if (/volante|folleto|brochure|triptico|diptico|flyer/.test(texto)) return '/images/servicios/folletos.svg';
    if (/banner|gigantografia|gigantografia|lona|pendon|roll.?up|cartel/.test(texto)) return '/images/servicios/banner.svg';
    if (/sticker|etiqueta|calcomania|adhesivo/.test(texto)) return '/images/servicios/etiquetas-stickers.svg';
    if (/taza|polo|camiseta|sublimacion|promocional/.test(texto)) return '/images/servicios/promocionales.svg';
    if (/libro|revista|catalogo|cuaderno|agenda|empastado/.test(texto)) return '/images/servicios/libros-revistas.svg';
    return '/images/servicios/impresion-general.svg';
  }

  // Modal "Solicitar"
  protected readonly elegido = signal<Servicio | null>(null);
  protected cantidad = 100;
  protected especificaciones = '';
  protected readonly guardando = signal(false);

  ngOnInit(): void {
    forkJoin({ categorias: this.catalogo.categorias(), servicios: this.catalogo.serviciosActivos() }).subscribe({
      next: ({ categorias, servicios }) => {
        this.categorias.set(categorias.filter((c) => c.activo !== false));
        this.servicios.set(servicios);
        this.cargando.set(false);
      },
      error: (err) => {
        this.cargando.set(false);
        this.notificador.error(err, 'No se pudo cargar el catálogo');
      },
    });
  }

  abrir(servicio: Servicio): void {
    this.cantidad = 100;
    this.especificaciones = '';
    this.elegido.set(servicio);
  }

  total(): number {
    return (this.elegido()?.precioBase ?? 0) * (this.cantidad || 0);
  }

  solicitar(): void {
    const servicio = this.elegido();
    if (!servicio || !this.cantidad || this.cantidad < 1) return;
    this.guardando.set(true);
    this.ventas
      .registrar({ idservicio: servicio.idservicio, cantidad: this.cantidad, especificaciones: this.especificaciones })
      .subscribe({
        next: (pedido) => {
          this.guardando.set(false);
          this.elegido.set(null);
          this.notificador.ok(`Pedido #${pedido.idpedido} registrado. Ahora registra tu pago.`);
          this.router.navigate(['/pedidos', pedido.idpedido]);
        },
        error: (err) => {
          this.guardando.set(false);
          this.notificador.error(err);
        },
      });
  }
}
