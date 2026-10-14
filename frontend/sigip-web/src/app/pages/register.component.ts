import { Component,inject,OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute,Router,RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { ApiService } from '../core/api.service';
@Component({selector:'sigip-register',standalone:true,imports:[CommonModule,FormsModule,RouterLink],templateUrl:'./register.component.html',styleUrls:['./auth-page.component.css']})
export class RegisterComponent implements OnInit {
 private api=inject(ApiService);private router=inject(Router);private route=inject(ActivatedRoute);requestedServiceId:number|null=null;
 ngOnInit():void{const id=Number(this.route.snapshot.queryParamMap.get('servicio'));this.requestedServiceId=id>0?id:null;}
 form={nomusuario:'',email:'',password:'',nombres:'',apellidos:''};loading=false;error='';success='';
 async submit():Promise<void>{this.error='';this.success='';this.loading=true;try{await firstValueFrom(this.api.register(this.form));this.success='Cuenta creada correctamente. Ya puedes iniciar sesión.';setTimeout(()=>void this.router.navigate(['/login'],{queryParams:this.requestedServiceId?{servicio:this.requestedServiceId}:{}}),900);}
 catch(error:any){this.error=error?.error?.message||error?.error?.detail||(error?.status===0?'No hay conexión con el backend. Verifica el gateway.':'No se pudo crear la cuenta. Comprueba que el usuario y correo no estén registrados.');}
 finally{this.loading=false;}}
}
