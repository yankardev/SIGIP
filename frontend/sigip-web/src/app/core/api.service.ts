import { Injectable,inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Categoria,Diseno,Insumo,LoginResponse,Notificacion,OrdenProduccion,Pedido,Pago,ServicioImpresion,Usuario,UsuarioAdminRequest } from './models';
@Injectable({providedIn:'root'})
export class ApiService {
 private readonly http=inject(HttpClient); private readonly base=environment.apiBaseUrl;
 private url(service:string,path:string):string{return `${this.base}/${service}/api/v1/${path}`;}
 login(body:{nomusuario:string;password:string}):Observable<LoginResponse>{return this.http.post<LoginResponse>(this.url('auth-service','auth/login'),body);}
 register(body:{nomusuario:string;email:string;password:string;nombres:string;apellidos:string}):Observable<Usuario>{return this.http.post<Usuario>(this.url('auth-service','auth/registro'),body);}
 users():Observable<Usuario[]>{return this.http.get<Usuario[]>(this.url('auth-service','usuarios'));}
 createUser(body:UsuarioAdminRequest):Observable<Usuario>{return this.http.post<Usuario>(this.url('auth-service','usuarios'),body);}
 updateUser(id:number,body:UsuarioAdminRequest):Observable<Usuario>{return this.http.put<Usuario>(this.url('auth-service',`usuarios/${id}`),body);}
 deleteUser(id:number):Observable<void>{return this.http.delete<void>(this.url('auth-service',`usuarios/${id}`));}
 health():Observable<unknown>{return this.http.get(`${this.base}/actuator/health`);}
 categories():Observable<Categoria[]>{return this.http.get<Categoria[]>(this.url('catalog-service','categorias'));}
 createCategory(body:{nombre:string;descripcion:string}):Observable<Categoria>{return this.http.post<Categoria>(this.url('catalog-service','categorias'),body);}
 updateCategory(id:number,body:{nombre:string;descripcion:string}):Observable<Categoria>{return this.http.put<Categoria>(this.url('catalog-service',`categorias/${id}`),body);}
 deleteCategory(id:number):Observable<void>{return this.http.delete<void>(this.url('catalog-service',`categorias/${id}`));}
 services(activeOnly=false):Observable<ServicioImpresion[]>{return this.http.get<ServicioImpresion[]>(this.url('catalog-service',activeOnly?'servicios/activos':'servicios'));}
 createService(body:{nombre:string;descripcion:string;precioBase:number;requiereCotizacion:boolean;idcategoria:number;activo:boolean}):Observable<ServicioImpresion>{return this.http.post<ServicioImpresion>(this.url('catalog-service','servicios'),body);}
 updateService(id:number,body:{nombre:string;descripcion:string;precioBase:number;requiereCotizacion:boolean;idcategoria:number;activo:boolean}):Observable<ServicioImpresion>{return this.http.put<ServicioImpresion>(this.url('catalog-service',`servicios/${id}`),body);}
 deleteService(id:number):Observable<void>{return this.http.delete<void>(this.url('catalog-service',`servicios/${id}`));}
 orders():Observable<Pedido[]>{return this.http.get<Pedido[]>(this.url('sales-service','pedidos'));}
 createOrder(body:{idservicio:number;cantidad:number;especificaciones:string}):Observable<Pedido>{return this.http.post<Pedido>(this.url('sales-service','pedidos'),body);}
 cancelOrder(id:number):Observable<Pedido>{return this.http.post<Pedido>(this.url('sales-service',`pedidos/${id}/cancelacion`),{});}
 addPayment(id:number,body:{monto:number;metodo:string;numeroOperacion:string}):Observable<Pago>{return this.http.post<Pago>(this.url('sales-service',`pedidos/${id}/pagos`),body);}
 paymentsByOrder(id:number):Observable<Pago[]>{return this.http.get<Pago[]>(this.url('sales-service',`pedidos/${id}/pagos`));}
 verifyPayment(id:number,body:{aprobado:boolean;observacion:string}):Observable<Pago>{return this.http.post<Pago>(this.url('sales-service',`pagos/${id}/verificacion`),body);}
 emitQuote(id:number,body:{precioUnitario:number;observacion:string}):Observable<Pedido>{return this.http.patch<Pedido>(this.url('sales-service',`pedidos/${id}/cotizacion`),body);}
 changeOrderState(id:number,body:{estado:string;observacion:string}):Observable<Pedido>{return this.http.patch<Pedido>(this.url('sales-service',`pedidos/${id}/estado`),body);}
 designs():Observable<Diseno[]>{return this.http.get<Diseno[]>(this.url('design-service','disenos'));}
 createDesign(body:{idpedido:number;nombreArchivo:string;urlArchivo:string;descripcion:string}):Observable<Diseno>{return this.http.post<Diseno>(this.url('design-service','disenos'),body);}
 decideDesign(id:number,body:{decision:'APROBAR'|'OBSERVAR';comentario:string}):Observable<Diseno>{return this.http.post<Diseno>(this.url('design-service',`disenos/${id}/decision`),body);}
 productionOrders():Observable<OrdenProduccion[]>{return this.http.get<OrdenProduccion[]>(this.url('production-service','ordenes-produccion'));}
 createProduction(body:{idpedido:number;prioridad:string;responsable:string;observacion:string}):Observable<OrdenProduccion>{return this.http.post<OrdenProduccion>(this.url('production-service','ordenes-produccion'),body);}
 startProduction(id:number):Observable<OrdenProduccion>{return this.http.post<OrdenProduccion>(this.url('production-service',`ordenes-produccion/${id}/inicio`),{});}
 productionQuality(id:number,body:{aprobado:boolean;observacion:string}):Observable<OrdenProduccion>{return this.http.post<OrdenProduccion>(this.url('production-service',`ordenes-produccion/${id}/calidad`),body);}
 supplies():Observable<Insumo[]>{return this.http.get<Insumo[]>(this.url('inventory-service','insumos'));}
 createSupply(body:{codigo:string;nombre:string;unidadMedida:string;stockActual:number;stockMinimo:number;activo:boolean}):Observable<Insumo>{return this.http.post<Insumo>(this.url('inventory-service','insumos'),body);}
 supplyMovement(id:number,body:{tipo:'ENTRADA'|'SALIDA';cantidad:number;motivo:string;idpedido?:number|null}):Observable<unknown>{return this.http.post(this.url('inventory-service',`insumos/${id}/movimientos`),body);}
 notifications():Observable<Notificacion[]>{return this.http.get<Notificacion[]>(this.url('notification-service','notificaciones'));}
 unreadCount():Observable<{noLeidas:number}>{return this.http.get<{noLeidas:number}>(this.url('notification-service','notificaciones/no-leidas'));}
 markRead(id:number):Observable<Notificacion>{return this.http.patch<Notificacion>(this.url('notification-service',`notificaciones/${id}/leida`),{});}
 reportDashboard():Observable<Record<string,unknown>>{return this.http.get<Record<string,unknown>>(this.url('report-service','reportes/dashboard'));}
}
