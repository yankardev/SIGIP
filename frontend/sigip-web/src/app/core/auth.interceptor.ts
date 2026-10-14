import { inject } from '@angular/core';
import { HttpInterceptorFn } from '@angular/common/http';
import { AuthService } from './auth.service';
export const authInterceptor:HttpInterceptorFn=(request,next)=>{
 const token=inject(AuthService).token;
 if(!token||request.url.endsWith('/auth/login')||request.url.endsWith('/auth/registro'))return next(request);
 return next(request.clone({setHeaders:{Authorization:`Bearer ${token}`}}));
};
