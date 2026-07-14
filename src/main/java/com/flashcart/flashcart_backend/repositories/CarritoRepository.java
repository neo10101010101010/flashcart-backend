package com.flashcart.flashcart_backend.repositories;

import com.flashcart.flashcart_backend.Enums.EstadoCarrito;
import com.flashcart.flashcart_backend.models.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    /**
     * Busca el carrito de un usuario según su estado
     * (por ejemplo, ACTIVO o PROCESADO).
     */
    Optional<Carrito> findByUsuarioIdAndEstado(Long usuarioId, EstadoCarrito estado);
}
