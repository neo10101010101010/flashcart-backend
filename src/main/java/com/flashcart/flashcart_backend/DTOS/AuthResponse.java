package com.flashcart.flashcart_backend.DTOS;

import lombok.Builder;
import lombok.Data;

/**
 * DTO utilizado para devolver la información de autenticación
 * al cliente después de un inicio de sesión o registro exitoso.
 * Contiene el token JWT, el nombre del usuario y su identificador.
 * Se utiliza @Builder para construir objetos de forma legible y
 * flexible, evitando constructores con múltiples parámetros.
 * Se utiliza @Data para generar automáticamente los métodos
 * getter, setter, toString(), equals() y hashCode(), reduciendo
 * la cantidad de código repetitivo.
 */
@Builder
@Data
public class AuthResponse {
    private String token;
    private String username;
    private Long userId;

}
