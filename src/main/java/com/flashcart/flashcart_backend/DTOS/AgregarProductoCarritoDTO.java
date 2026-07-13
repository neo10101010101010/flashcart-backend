package com.flashcart.flashcart_backend.DTOS;

import com.flashcart.flashcart_backend.models.Productos;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public class AgregarProductoCarritoDTO {
    @Schema(
            description = "obtiene id de producto",
            example = "1"
    )
    private Productos productos;
    @Schema(
            description = "numero de productos agregados al carrito",
            example = "1"
    )
    private Integer cantidad;

    public AgregarProductoCarritoDTO() {
    }

    public AgregarProductoCarritoDTO(Productos productos, Integer cantidad) {
        this.productos = productos;
        this.cantidad = cantidad;
    }

    public Productos getProductos() {
        return productos;
    }

    public void setProductos(Productos productos) {
        this.productos = productos;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
