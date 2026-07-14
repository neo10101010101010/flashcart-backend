package com.flashcart.flashcart_backend.models;

import com.flashcart.flashcart_backend.Enums.EstadoCarrito;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Carrito")
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relación Muchos a Uno entre Carrito y Usuario.
     * Cada carrito pertenece a un único usuario, mientras que un usuario
     * puede tener uno o varios carritos a lo largo del tiempo.
     * Se utiliza FetchType.LAZY para cargar la información del usuario
     * únicamente cuando sea necesaria, optimizando el rendimiento de la aplicación.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoCarrito estado = EstadoCarrito.ACTIVO;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Relación Uno a Muchos entre Carrito y CarritoItems.
     * Un carrito puede contener múltiples productos representados por
     * la entidad CarritoItems.
     * - mappedBy indica que la relación es administrada por la propiedad
     *   "carrito" de CarritoItems.
     * - CascadeType.ALL propaga las operaciones (crear, actualizar y eliminar)
     *   a los elementos del carrito.
     * - orphanRemoval elimina automáticamente los elementos que dejan de
     *   pertenecer al carrito.
     * - FetchType.LAZY carga los productos únicamente cuando son solicitados,
     *   mejorando el rendimiento.
     */
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CarritoItems> items = new ArrayList<>();

    public Carrito() {
    }

    public Carrito(Long id, Usuario usuario, EstadoCarrito estado, LocalDateTime createdAt,
                   LocalDateTime updatedAt, List<CarritoItems> items) {
        this.id = id;
        this.usuario = usuario;
        this.estado = estado;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public EstadoCarrito getEstado() {
        return estado;
    }

    public void setEstado(EstadoCarrito estado) {
        this.estado = estado;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<CarritoItems> getItems() {
        return items;
    }

    public void setItems(List<CarritoItems> items) {
        this.items = items;
    }
}
