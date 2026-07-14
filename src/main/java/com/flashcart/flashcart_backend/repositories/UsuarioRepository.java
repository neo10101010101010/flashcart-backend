package com.flashcart.flashcart_backend.repositories;

import com.flashcart.flashcart_backend.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    /**
     * Busca un usuario por su nombre de usuario.
     */
    Optional<Usuario> findByUsername(String username);
    /**
     * Verifica si ya existe un usuario con el nombre indicado.
     */
    boolean existsByUsername(String username);
}
