import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'sigip-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./auth-page.component.css']
})
export class LoginComponent implements OnInit {
  private api = inject(ApiService);
  private auth = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  isInternal = false;
  form = { nomusuario: '', password: '' };
  loading = false;
  error = '';

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.isInternal = data['internal'] === true;
      this.error = '';
    });
  }

  async submit(): Promise<void> {
    this.error = '';
    this.loading = true;

    try {
      const response = await firstValueFrom(this.api.login(this.form));
      const roles = (response.usuario.roles ?? []).map(role =>
        role.replace(/^ROLE_/i, '').toUpperCase()
      );
      const staffRoles = ['ADMIN', 'VENTAS', 'CAJA', 'DISENO', 'PRODUCCION', 'ALMACEN'];
      const isStaff = roles.some(role => staffRoles.includes(role));
      const isClient = roles.includes('CLIENTE') && !isStaff;

      if (this.isInternal && !isStaff) {
        this.error = 'Esta cuenta no tiene un rol de personal interno. Utiliza el acceso de clientes.';
        return;
      }

      if (!this.isInternal && !isClient) {
        this.error = 'Esta cuenta pertenece al personal interno. Utiliza el acceso exclusivo para el personal.';
        return;
      }

      this.auth.setSession(response.token, response.usuario);

      const serviceId = Number(this.route.snapshot.queryParamMap.get('servicio'));
      if (!this.isInternal && serviceId > 0 && this.auth.isClient) {
        await this.router.navigate(['/pedidos'], { queryParams: { servicio: serviceId } });
      } else {
        await this.router.navigate(['/dashboard']);
      }
    } catch (error: any) {
      this.error = error?.error?.message
        || error?.error?.detail
        || (error?.status === 0
          ? 'No se pudo conectar con el backend. Comprueba Docker y el API Gateway en http://localhost:8090.'
          : 'No se pudo iniciar sesión. Revisa tu usuario y contraseña.');
    } finally {
      this.loading = false;
    }
  }
}
