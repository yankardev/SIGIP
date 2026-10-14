import { Injectable } from '@angular/core';
import { Usuario } from './models';
@Injectable({providedIn:'root'})
export class AuthService {
 private readonly tokenKey='sigip_token'; private readonly userKey='sigip_user';
 get token():string|null{return localStorage.getItem(this.tokenKey);}
 get user():Usuario|null{const raw=localStorage.getItem(this.userKey);if(!raw)return null;try{return JSON.parse(raw) as Usuario;}catch{return null;}}
 get isAuthenticated():boolean{return Boolean(this.token);}
 setSession(token:string,user:Usuario):void{localStorage.setItem(this.tokenKey,token);localStorage.setItem(this.userKey,JSON.stringify(user));}
 logout():void{localStorage.removeItem(this.tokenKey);localStorage.removeItem(this.userKey);}
 get isAdmin():boolean{return (this.user?.roles??[]).some(role=>['ADMIN','ROLE_ADMIN'].includes(role.toUpperCase()));}
}
