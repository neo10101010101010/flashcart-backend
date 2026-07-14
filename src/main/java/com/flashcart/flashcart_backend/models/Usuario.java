package com.flashcart.flashcart_backend.models;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "Usuario")
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;

    public Usuario() {
    }

    public Usuario(Long id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Devuelve los roles o permisos asignados al usuario.
     * En este proyecto todos los usuarios autenticados tienen el rol ROLE_USER,
     * el cual es utilizado por Spring Security para autorizar el acceso a los
     * recursos protegidos.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * Indica si la cuenta del usuario no ha expirado.
     * Retorna true porque actualmente la aplicación no implementa
     * expiración de cuentas.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica si la cuenta del usuario no está bloqueada.
     * Retorna true porque la aplicación no contempla el bloqueo
     * de cuentas por intentos fallidos u otras restricciones.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica si las credenciales del usuario (contraseña)
     * continúan siendo válidas.
     * Retorna true porque la aplicación no implementa
     * expiración periódica de contraseñas.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica si el usuario se encuentra habilitado.
     * Retorna true porque todos los usuarios registrados
     * pueden autenticarse mientras existan en la base de datos.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
