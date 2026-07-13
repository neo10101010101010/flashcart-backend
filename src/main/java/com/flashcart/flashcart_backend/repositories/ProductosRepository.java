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
    //Se obtiene listado de productos disponibles eb el catalogo
    List<Productos> findByStockGreaterThan(Integer stockMinimo);

    //Se verifica la informacion del stock
    @Query("SELECT p.stock FROM Productos p WHERE p.id = :id")
    Integer findStockById(@Param("id") UUID id);

    //Calculo de decremento del stock disponible
    @Modifying
    @Query("UPDATE Productos p SET p.stock = p.stock - :cantidad WHERE p.id = :id AND p.stock >= :cantidad")
    int decrementarStock(@Param("id") UUID id, @Param("cantidad") Integer cantidad);

    //Restaura el stock disponible al eliminar el carrito creado
    @Modifying
    @Query("UPDATE Productos p SET p.stock = p.stock + :cantidad WHERE p.id = :id")
    int incrementarStock(@Param("id") UUID id, @Param("cantidad") Integer cantidad);
}
