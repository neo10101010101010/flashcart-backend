package com.flashcart.flashcart_backend.repositories;

import com.flashcart.flashcart_backend.models.CarritoItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItems, Long> {
    /**
     * Obtiene todos los productos asociados a un carrito específico.
     */
    List<CarritoItems> findByCarritoId(Long carritoId);
    /**
     * Busca un producto dentro de un carrito mediante el ID del carrito
     * y el ID del producto.
     */
    Optional<CarritoItems> findByCarritoIdAndProductoId(Long carritoId, UUID productoId);
}
