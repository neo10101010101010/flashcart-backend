package com.flashcart.flashcart_backend.DTOS;

import com.flashcart.flashcart_backend.Enums.EstadoCarrito;
import com.flashcart.flashcart_backend.models.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

public class CarritoDTO {

    private Long id;
    @Schema(
            description = "Identificador del usuario",
            example = "1"
    )
    private Long usuario_id;
    @Schema(
            description = "Estado del carrito",
            example = "ACTIVO"
    )
    private EstadoCarrito estado = EstadoCarrito.ACTIVO;
    @Schema(
            description = "Obtiene el subtotal del producto",
            example = "1000.00"
    )
    private BigDecimal subtotal;
    @Schema(
            description = "Determina el iva a cobrar",
            example = "0.16"
    )
    private BigDecimal iva;
    @Schema(
            description = "Calculo del total",
            example = "1,160.00"
    )
    private BigDecimal total;
    @Schema(
            description = "Cantidad de articulos",
            example = "1"
    )
    private Integer cantidadArticulos;

    private List<CarritoItemsDTO> items;

    public CarritoDTO() {
    }

    public CarritoDTO(Long id, Long usuario_id, EstadoCarrito estado, BigDecimal subtotal, BigDecimal iva,
                      BigDecimal total, Integer cantidadArticulos, List<CarritoItemsDTO> items) {
        this.id = id;
        this.usuario_id = usuario_id;
        this.estado = estado;
        this.subtotal = subtotal;
        this.iva = iva;
        this.total = total;
        this.cantidadArticulos = cantidadArticulos;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuario_id() {
        return usuario_id;
    }

    public void setUsuario_id(Long usuario_id) {
        this.usuario_id = usuario_id;
    }

    public EstadoCarrito getEstado() {
        return estado;
    }

    public void setEstado(EstadoCarrito estado) {
        this.estado = estado;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Integer getCantidadArticulos() {
        return cantidadArticulos;
    }

    public void setCantidadArticulos(Integer cantidadArticulos) {
        this.cantidadArticulos = cantidadArticulos;
    }

    public List<CarritoItemsDTO> getItems() {
        return items;
    }

    public void setItems(List<CarritoItemsDTO> items) {
        this.items = items;
    }
}
