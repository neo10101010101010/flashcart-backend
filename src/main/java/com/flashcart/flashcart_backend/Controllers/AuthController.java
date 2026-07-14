package com.flashcart.flashcart_backend.Controllers;

import com.flashcart.flashcart_backend.DTOS.AuthRequest;
import com.flashcart.flashcart_backend.DTOS.AuthResponse;
import com.flashcart.flashcart_backend.DTOS.RegisterRequest;
import com.flashcart.flashcart_backend.exceptions.UsuarioExistenteException;
import com.flashcart.flashcart_backend.models.Usuario;
import com.flashcart.flashcart_backend.repositories.UsuarioRepository;
import com.flashcart.flashcart_backend.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Usuario", description = "Operaciones relacionadas con los usuarios ")
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Operation(
            summary = "Logueo de usuarios",
            description = "agrega credenciales de usuarios para iniciar sesion"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "inicio de sesion exitosa"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        System.out.println("ENTRO AL LOGIN");
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        //UserDetails user = usuarioRepository.findByUsername(request.getUsername()).orElseThrow();
        //String token = jwtService.generateToken(user);
        System.out.println("AUTENTICACIÓN EXITOSA");
        Usuario user = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow();

        String token = jwtService.generateToken(user, user.getId());

        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .userId(user.getId())
                .build());
    }

    @Operation(
            summary = "Registro de usuarios",
            description = "agrega datos de usuario para su registro"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Registro de usuario exitoso"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new UsuarioExistenteException("El usuario ya existe");
        }

        Usuario usuario = new Usuario();
        usuario.setId(request.getId());
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        usuarioRepository.save(usuario);
        String token = jwtService.generateToken(usuario, usuario.getId());

        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .username(usuario.getUsername())
                .userId(usuario.getId())
                .build());
    }
}
