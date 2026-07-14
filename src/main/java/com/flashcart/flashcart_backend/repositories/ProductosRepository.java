package com.flashcart.flashcart_backend.repositories;

import com.flashcart.flashcart_backend.models.Productos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductosRepository extends JpaRepository<Productos, UUID> {
    /**
     * Obtiene los productos con stock disponible para su venta.
     */
    List<Productos> findByStockGreaterThan(Integer stockMinimo);
    /**
     * Consulta la cantidad de stock disponible de un producto.
     */
    @Query("SELECT p.stock FROM Productos p WHERE p.id = :id")
    Integer findStockById(@Param("id") UUID id);
    /**
     * Reduce el stock de un producto al agregarlo al carrito,
     * siempre que exista suficiente inventario.
     */
    @Modifying
    @Query("UPDATE Productos p SET p.stock = p.stock - :cantidad WHERE p.id = :id AND p.stock >= :cantidad")
    int decrementarStock(@Param("id") UUID id, @Param("cantidad") Integer cantidad);
    /**
     * Restaura el stock de un producto al eliminarlo del carrito
     * o cancelar la operación.
     */
    @Modifying
    @Query("UPDATE Productos p SET p.stock = p.stock + :cantidad WHERE p.id = :id")
    int incrementarStock(@Param("id") UUID id, @Param("cantidad") Integer cantidad);
}
