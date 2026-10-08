package pe.edu.cibertec.sigip.authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.sigip.authservice.dto.*;
import pe.edu.cibertec.sigip.authservice.model.Usuario;
import pe.edu.cibertec.sigip.authservice.repository.UsuarioRepository;
import pe.edu.cibertec.sigip.authservice.security.JwtService;
import pe.edu.cibertec.sigip.authservice.service.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(
            @Valid @RequestBody RegistroUsuarioRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.registrarCliente(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.nomusuario(),
                        request.password()
                )
        );

        var authorities = authentication.getAuthorities().stream().toList();

        String token = jwtService.generarToken(
                authentication.getName(),
                authorities
        );

        Usuario usuario = usuarioRepository.findByNomusuario(authentication.getName())
                .orElseThrow();

        return ResponseEntity.ok(
                new LoginResponse(
                        token,
                        "Bearer",
                        UsuarioResponse.from(usuario)
                )
        );
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(Authentication authentication) {
        Usuario usuario = usuarioRepository.findByNomusuario(authentication.getName())
                .orElseThrow();

        return ResponseEntity.ok(UsuarioResponse.from(usuario));
    }

    @GetMapping("/authorities")
    public ResponseEntity<List<String>> authorities(Authentication authentication) {
        return ResponseEntity.ok(
                authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );
    }
}
