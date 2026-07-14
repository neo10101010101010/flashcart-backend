package com.flashcart.flashcart_backend.DTOS;

import com.flashcart.flashcart_backend.models.Carrito;
import com.flashcart.flashcart_backend.models.Productos;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

public class CarritoItemsDTO {
    private Long id;
    @Schema(
            description = "ID del carrito",
            example = "1"
    )
    private Long carrito_id;
    @Schema(
            description = "ID del producto agregado",
            example = "3"
    )
    private UUID producto_id;
    @Schema(
            description = "Cantidad de productos agregados",
            example = "1"
    )
    private Integer cantidad;
    @Schema(
            description = "Precio unitario",
            example = "1,160.00"
    )
    private BigDecimal precioUnitario;

    public CarritoItemsDTO() {
    }

    public CarritoItemsDTO(Long id, Long carrito_id, UUID producto_id, Integer cantidad, BigDecimal precioUnitario) {
        this.id = id;
        this.carrito_id = carrito_id;
        this.producto_id = producto_id;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCarrito_id() {
        return carrito_id;
    }

    public void setCarrito_id(Long carrito_id) {
        this.carrito_id = carrito_id;
    }

    public UUID getProducto_id() {
        return producto_id;
    }

    public void setProducto_id(UUID producto_id) {
        this.producto_id = producto_id;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
