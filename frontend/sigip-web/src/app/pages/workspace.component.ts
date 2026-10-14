import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { firstValueFrom, Observable } from 'rxjs';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { Categoria, Diseno, Insumo, Notificacion, OrdenProduccion, Pedido, ServicioImpresion } from '../core/models';

interface MenuItem { label:string; path:string; icon:string; admin?:boolean; }
@Component({selector:'sigip-workspace',standalone:true,imports:[CommonModule,FormsModule,RouterLink,RouterLinkActive],templateUrl:'./workspace.component.html',styleUrls:['./workspace.component.css']})
export class WorkspaceComponent implements OnInit {
 readonly auth=inject(AuthService);private api=inject(ApiService);private route=inject(ActivatedRoute);private router=inject(Router);
 page='dashboard';loading=false;error='';success='';backendStatus='Sin comprobar';private failures=0;search='';
 categories:Categoria[]=[];services:ServicioImpresion[]=[];orders:Pedido[]=[];designs:Diseno[]=[];productionOrders:OrdenProduccion[]=[];supplies:Insumo[]=[];notifications:Notificacion[]=[];unreadCount=0;report:Record<string,unknown>|null=null;
 categoryForm={nombre:'',descripcion:''};editingCategoryId:number|null=null;
 serviceForm={nombre:'',descripcion:'',precioBase:0,requiereCotizacion:false,idcategoria:0,activo:true};editingServiceId:number|null=null;
 orderForm:{idservicio:number|null;cantidad:number;especificaciones:string}={idservicio:null,cantidad:1,especificaciones:''};
 paymentOrderId:number|null=null;paymentForm={monto:0,metodo:'YAPE',numeroOperacion:''};
 designForm={idpedido:0,nombreArchivo:'',urlArchivo:'',descripcion:''};
 productionForm={idpedido:0,prioridad:'MEDIA',responsable:'',observacion:''};
 supplyForm={codigo:'',nombre:'',unidadMedida:'unidad',stockActual:0,stockMinimo:0,activo:true};
 movementSupplyId:number|null=null;movementForm:{tipo:'ENTRADA'|'SALIDA';cantidad:number;motivo:string;idpedido:number|null}={tipo:'ENTRADA',cantidad:1,motivo:'',idpedido:null};
 readonly menu:MenuItem[]=[
  {label:'Resumen',path:'/dashboard',icon:'▦'},{label:'Catálogo',path:'/catalogo',icon:'▤'},{label:'Pedidos',path:'/pedidos',icon:'▣'},
  {label:'Diseños',path:'/disenos',icon:'◈'},{label:'Notificaciones',path:'/notificaciones',icon:'♧'},
  {label:'Categorías',path:'/categorias',icon:'◫',admin:true},{label:'Servicios',path:'/servicios',icon:'✳',admin:true},
  {label:'Producción',path:'/produccion',icon:'⚙',admin:true},{label:'Inventario',path:'/inventario',icon:'▧',admin:true},{label:'Reportes',path:'/reportes',icon:'▥',admin:true}
 ];
 get isAdmin():boolean{return this.auth.isAdmin;}
 get visibleMenu():MenuItem[]{return this.menu.filter(item=>!item.admin||this.isAdmin);}
 get pageTitle():string{return ({dashboard:'Resumen general',catalogo:'Catálogo de impresión',pedidos:'Gestión de pedidos',categorias:'Categorías',servicios:'Servicios de impresión',disenos:'Diseños y aprobaciones',produccion:'Control de producción',inventario:'Inventario de insumos',notificaciones:'Centro de notificaciones',reportes:'Reportes y métricas'} as Record<string,string>)[this.page]||'SIGIP';}
 get userName():string{const u=this.auth.user;return [u?.nombres,u?.apellidos].filter(Boolean).join(' ')||u?.nomusuario||'Usuario';}
 get userInitials():string{return this.userName.split(' ').filter(Boolean).slice(0,2).map(s=>s[0].toUpperCase()).join('')||'S';}
 get filteredOrders():Pedido[]{const t=this.search.trim().toLowerCase();return t?this.orders.filter(o=>`${o.idpedido} ${o.nombreServicio} ${o.estado} ${o.cliente||''}`.toLowerCase().includes(t)):this.orders;}
 get lowStockCount():number{return this.supplies.filter(s=>s.bajoStock).length;}
 ngOnInit():void{this.route.data.subscribe(data=>{this.page=String(data['page']||'dashboard');void this.loadPage();});}
 private async safe<T>(request:Observable<T>,fallback:T):Promise<T>{try{return await firstValueFrom(request);}catch{this.failures++;return fallback;}}
 async loadPage():Promise<void>{
  this.loading=true;this.error='';this.failures=0;
  try{
   switch(this.page){
    case'dashboard':
     [this.orders,this.services]=await Promise.all([this.safe(this.api.orders(),[]),this.safe(this.api.services(true),[])]);
     this.unreadCount=Number((await this.safe(this.api.unreadCount(),{noLeidas:0})).noLeidas||0);
     if(this.isAdmin){this.report=await this.safe(this.api.reportDashboard(),{});this.supplies=await this.safe(this.api.supplies(),[]);}
     break;
    case'catalogo':[this.services,this.categories]=await Promise.all([this.safe(this.api.services(true),[]),this.safe(this.api.categories(),[])]);break;
    case'pedidos':[this.orders,this.services]=await Promise.all([this.safe(this.api.orders(),[]),this.safe(this.api.services(true),[])]);break;
    case'categorias':this.categories=await this.safe(this.api.categories(),[]);break;
    case'servicios':[this.services,this.categories]=await Promise.all([this.safe(this.api.services(false),[]),this.safe(this.api.categories(),[])]);break;
    case'disenos':this.designs=await this.safe(this.api.designs(),[]);break;
    case'produccion':this.productionOrders=await this.safe(this.api.productionOrders(),[]);break;
    case'inventario':this.supplies=await this.safe(this.api.supplies(),[]);break;
    case'notificaciones':
     this.notifications=await this.safe(this.api.notifications(),[]);
     this.unreadCount=Number((await this.safe(this.api.unreadCount(),{noLeidas:0})).noLeidas||0);break;
    case'reportes':
     [this.report,this.orders,this.supplies]=await Promise.all([this.safe(this.api.reportDashboard(),{}),this.safe(this.api.orders(),[]),this.safe(this.api.supplies(),[])]);break;
   }
   if(this.failures)this.error='No se pudieron cargar algunos datos. Comprueba que los microservicios estén activos y vuelve a actualizar.';
  }finally{this.loading=false;}
 }
 async checkBackend():Promise<void>{this.backendStatus='Comprobando...';try{await firstValueFrom(this.api.health());this.backendStatus='Gateway conectado';this.success='El API Gateway respondió correctamente.';this.error='';}catch{this.backendStatus='Sin conexión';this.error='No se pudo consultar /actuator/health. Verifica api-gateway en el puerto 8090.';}}
 logout():void{this.auth.logout();void this.router.navigate(['/login']);}
 selectService(s:ServicioImpresion):void{this.orderForm.idservicio=s.idservicio;this.orderForm.cantidad=1;void this.router.navigate(['/pedidos']);}
 async saveCategory():Promise<void>{this.error='';this.success='';try{if(this.editingCategoryId){await firstValueFrom(this.api.updateCategory(this.editingCategoryId,this.categoryForm));this.success='Categoría actualizada.';}else{await firstValueFrom(this.api.createCategory(this.categoryForm));this.success='Categoría creada.';}this.cancelCategoryEdit();await this.loadPage();}catch(e:any){this.error=this.message(e,'No se pudo guardar la categoría. Verifica el rol ADMIN.');}}
 editCategory(c:Categoria):void{this.editingCategoryId=c.idcategoria;this.categoryForm={nombre:c.nombre,descripcion:c.descripcion||''};window.scrollTo({top:0,behavior:'smooth'});}
 cancelCategoryEdit():void{this.editingCategoryId=null;this.categoryForm={nombre:'',descripcion:''};}
 async deleteCategory(c:Categoria):Promise<void>{if(!confirm(`¿Eliminar la categoría "${c.nombre}"?`))return;try{await firstValueFrom(this.api.deleteCategory(c.idcategoria));this.success='Categoría eliminada.';await this.loadPage();}catch(e:any){this.error=this.message(e,'No se pudo eliminar; puede tener servicios asociados.');}}
 editService(s:ServicioImpresion):void{this.editingServiceId=s.idservicio;this.serviceForm={nombre:s.nombre,descripcion:s.descripcion||'',precioBase:Number(s.precioBase||0),requiereCotizacion:Boolean(s.requiereCotizacion),idcategoria:Number(s.idcategoria||0),activo:Boolean(s.activo)};window.scrollTo({top:0,behavior:'smooth'});}
 cancelServiceEdit():void{this.editingServiceId=null;this.serviceForm={nombre:'',descripcion:'',precioBase:0,requiereCotizacion:false,idcategoria:0,activo:true};}
 async saveService():Promise<void>{this.error='';this.success='';const body={...this.serviceForm,precioBase:Number(this.serviceForm.precioBase),idcategoria:Number(this.serviceForm.idcategoria)};try{if(this.editingServiceId){await firstValueFrom(this.api.updateService(this.editingServiceId,body));this.success='Servicio actualizado.';}else{await firstValueFrom(this.api.createService(body));this.success='Servicio creado.';}this.cancelServiceEdit();await this.loadPage();}catch(e:any){this.error=this.message(e,'No se pudo guardar el servicio. Revisa categoría y datos.');}}
 async deleteService(s:ServicioImpresion):Promise<void>{if(!confirm(`¿Eliminar el servicio "${s.nombre}"?`))return;try{await firstValueFrom(this.api.deleteService(s.idservicio));this.success='Servicio eliminado.';await this.loadPage();}catch(e:any){this.error=this.message(e,'No se pudo eliminar el servicio.');}}
 async createOrder():Promise<void>{this.error='';this.success='';if(!this.orderForm.idservicio){this.error='Selecciona un servicio del catálogo.';return;}try{const o=await firstValueFrom(this.api.createOrder({idservicio:Number(this.orderForm.idservicio),cantidad:Number(this.orderForm.cantidad),especificaciones:this.orderForm.especificaciones}));this.success=`Pedido #${o.idpedido} registrado correctamente.`;this.orderForm={idservicio:null,cantidad:1,especificaciones:''};await this.loadPage();}catch(e:any){this.error=this.message(e,'No se pudo registrar el pedido.');}}
 startPayment(o:Pedido):void{this.paymentOrderId=o.idpedido;this.paymentForm={monto:Number(o.total||0),metodo:'YAPE',numeroOperacion:''};}
 async submitPayment():Promise<void>{if(!this.paymentOrderId)return;this.error='';try{const id=this.paymentOrderId;await firstValueFrom(this.api.addPayment(id,{monto:Number(this.paymentForm.monto),metodo:this.paymentForm.metodo,numeroOperacion:this.paymentForm.numeroOperacion.trim()}));this.success=`Pago del pedido #${id} registrado para revisión manual. No se procesa dinero desde SIGIP.`;this.paymentOrderId=null;await this.loadPage();}catch(e:any){this.error=this.message(e,'No se pudo registrar el pago.');}}
 async cancelOrder(o:Pedido):Promise<void>{if(!confirm(`¿Solicitar la cancelación del pedido #${o.idpedido}?`))return;try{await firstValueFrom(this.api.cancelOrder(o.idpedido));this.success=`Se procesó la cancelación del pedido #${o.idpedido}.`;await this.loadPage();}catch(e:any){this.error=this.message(e,'No se pudo cancelar el pedido.');}}
 async createDesign():Promise<void>{try{await firstValueFrom(this.api.createDesign({idpedido:Number(this.designForm.idpedido),nombreArchivo:this.designForm.nombreArchivo.trim(),urlArchivo:this.designForm.urlArchivo.trim(),descripcion:this.designForm.descripcion.trim()}));this.success='Diseño registrado. Se guarda una URL; no se sube el archivo binario.';this.designForm={idpedido:0,nombreArchivo:'',urlArchivo:'',descripcion:''};await this.loadPage();}catch(e:any){this.error=this.message(e,'No se pudo registrar el diseño.');}}
 async decideDesign(d:Diseno,decision:'APROBAR'|'OBSERVAR'):Promise<void>{const comentario=prompt(decision==='APROBAR'?'Comentario opcional de aprobación:':'Indica qué debe corregirse:');if(comentario===null)return;try{await firstValueFrom(this.api.decideDesign(d.iddiseno,{decision,comentario}));this.success=`Diseño #${d.iddiseno}: ${decision==='APROBAR'?'aprobado':'observado'}.`;await this.loadPage();}catch(e:any){this.error=this.message(e,'No se pudo registrar la decisión.');}}
 async createProduction():Promise<void>{try{await firstValueFrom(this.api.createProduction({idpedido:Number(this.productionForm.idpedido),prioridad:this.productionForm.prioridad,responsable:this.productionForm.responsable.trim(),observacion:this.productionForm.observacion.trim()}));this.success='Orden de producción creada.';this.productionForm={idpedido:0,prioridad:'MEDIA',responsable:'',observacion:''};await this.loadPage();}catch(e:any){this.error=this.message(e,'No se pudo crear la orden de producción.');}}
 async startProduction(o:OrdenProduccion):Promise<void>{try{await firstValueFrom(this.api.startProduction(o.idorden));this.success=`Producción de la orden #${o.idorden} iniciada.`;await this.loadPage();}catch(e:any){this.error=this.message(e,'No se pudo iniciar la producción.');}}
 async qualityCheck(o:OrdenProduccion):Promise<void>{const approved=confirm(`Control de calidad de la orden #${o.idorden}.\\nAceptar = APROBADO. Cancelar = OBSERVADO.`);const observacion=prompt('Observación de calidad (opcional):')??'';try{await firstValueFrom(this.api.productionQuality(o.idorden,{aprobado:approved,observacion}));this.success='Resultado de calidad registrado.';await this.loadPage();}catch(e:any){this.error=this.message(e,'No se pudo registrar el control de calidad.');}}
 async createSupply():Promise<void>{try{await firstValueFrom(this.api.createSupply({...this.supplyForm,stockActual:Number(this.supplyForm.stockActual),stockMinimo:Number(this.supplyForm.stockMinimo)}));this.success='Insumo registrado.';this.supplyForm={codigo:'',nombre:'',unidadMedida:'unidad',stockActual:0,stockMinimo:0,activo:true};await this.loadPage();}catch(e:any){this.error=this.message(e,'No se pudo registrar el insumo.');}}
 openMovement(s:Insumo):void{this.movementSupplyId=s.idinsumo;this.movementForm={tipo:'ENTRADA',cantidad:1,motivo:'',idpedido:null};}
 async submitMovement():Promise<void>{if(!this.movementSupplyId)return;try{await firstValueFrom(this.api.supplyMovement(this.movementSupplyId,{tipo:this.movementForm.tipo,cantidad:Number(this.movementForm.cantidad),motivo:this.movementForm.motivo.trim(),idpedido:this.movementForm.idpedido?Number(this.movementForm.idpedido):null}));this.success='Movimiento de inventario registrado.';this.movementSupplyId=null;await this.loadPage();}catch(e:any){this.error=this.message(e,'No se pudo registrar el movimiento.');}}
 async markRead(n:Notificacion):Promise<void>{try{await firstValueFrom(this.api.markRead(n.idnotificacion));this.success='Notificación marcada como leída.';await this.loadPage();}catch(e:any){this.error=this.message(e,'No se pudo actualizar la notificación.');}}
 reportEntries():Array<[string,unknown]>{return this.report?Object.entries(this.report):[];}
 statusClass(value:string|undefined):string{return `status-${String(value||'sin-estado').toLowerCase().replaceAll('_','-')}`;}
 formatDate(value?:string):string{if(!value)return '—';const d=new Date(value);return Number.isNaN(d.getTime())?value:d.toLocaleString('es-PE',{dateStyle:'medium',timeStyle:'short'});}
 private message(e:any,fallback:string):string{return e?.error?.message||e?.error?.detail||e?.error?.error||fallback;}
}
