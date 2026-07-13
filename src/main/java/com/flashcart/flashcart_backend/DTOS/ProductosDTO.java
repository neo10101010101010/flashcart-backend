package com.flashcart.flashcart_backend.DTOS;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductosDTO {
    @Schema(
            description = "Identificador del producto",
            example = "8a7ac1fb-8f51-41f8-a9c6-ebf7bcfa679b"
    )
    private UUID id;
    @Schema(
            description = "Nombre del producto",
            example = "Laptop Lenovo"
    )
    private String nombre;
    @Schema(
            description = "Descripción del producto",
            example = "Laptop con procesador Intel Core i7"
    )
    private String descripcion;
    @Schema(
            description = "Precio del producto",
            example = "15999.99"
    )
    private BigDecimal precio;
    @Schema(
            description = "Cantidad disponible en inventario",
            example = "25"
    )
    private Integer stock;

    public ProductosDTO() {
    }

    public ProductosDTO(UUID id, String nombre, String descripcion, BigDecimal precio, Integer stock) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
