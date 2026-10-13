package pe.edu.cibertec.sigip.productionservice.security;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
@Component @RequiredArgsConstructor
public class FiltroJwtAuth extends OncePerRequestFilter{
 private final JwtService jwt;
 protected void doFilterInternal(HttpServletRequest r,HttpServletResponse s,FilterChain c)throws ServletException,IOException{
  String h=r.getHeader("Authorization");
  if(h!=null&&h.startsWith("Bearer ")){try{String t=h.substring(7);if(jwt.valid(t)){var a=jwt.authorities(t).stream().map(SimpleGrantedAuthority::new).toList();SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(jwt.user(t),null,a));}}catch(Exception e){SecurityContextHolder.clearContext();}}
  c.doFilter(r,s);
 }
}