import { Component,inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router,RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
@Component({selector:'sigip-login',standalone:true,imports:[CommonModule,FormsModule,RouterLink],templateUrl:'./login.component.html',styleUrls:['./auth-page.component.css']})
export class LoginComponent {
 private api=inject(ApiService);private auth=inject(AuthService);private router=inject(Router);
 form={nomusuario:'',password:''};loading=false;error='';
 async submit():Promise<void>{this.error='';this.loading=true;try{const response=await firstValueFrom(this.api.login(this.form));this.auth.setSession(response.token,response.usuario);await this.router.navigate(['/dashboard']);}
 catch(error:any){this.error=error?.error?.message||error?.error?.detail||(error?.status===0?'No se pudo conectar con el backend. Comprueba Docker y el API Gateway en http://localhost:8090.':'No se pudo iniciar sesión. Revisa tu usuario y contraseña.');}
 finally{this.loading=false;}}
}
