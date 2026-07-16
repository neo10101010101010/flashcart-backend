package com.flashcart.flashcart_backend.services;

import com.flashcart.flashcart_backend.DTOS.ProductosDTO;
import com.flashcart.flashcart_backend.models.Productos;
import com.flashcart.flashcart_backend.repositories.ProductosRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class ProductosService {
    private final ProductosRepository productosRepository;

    @Autowired
    public ProductosService (ProductosRepository productosRepository){
        this.productosRepository = productosRepository;
    }

    //crear producto con validacion de stock no negativo
    public ProductosDTO crearProducto(ProductosDTO producto){
        log.info("Creando nuevo producto: {}", producto.getNombre());
        validarStockPositivo(producto.getStock());
        Productos entity = mapToEntity(producto);
        Productos saved = productosRepository.save(entity);
        log.info("Producto creado con ID: {}", saved.getId());
        return mapToDTO(saved);
    }

    private void validarStockPositivo(Integer stock){
        if(stock != null && stock < 0){
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
    }

    private Productos mapToEntity(ProductosDTO dto) {
        Productos entity = new Productos();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setPrecio(dto.getPrecio());
        entity.setStock(dto.getStock());
        return entity;
    }

    private ProductosDTO mapToDTO(Productos entity) {
        return new ProductosDTO(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getPrecio(),
                entity.getStock()
        );
    }


    //listar todos los productos
    public List <ProductosDTO> listarTodos(){
        log.debug("Listando todos los productos");
        return productosRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    //listar productos para catalogo con stock mayor a 0
    public List <ProductosDTO> listarDisponibles(){
        log.debug("Listando productos disponibles (stock > 0)");
        return productosRepository.findByStockGreaterThan(0)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }


    public ProductosDTO actualizarProductos(UUID id, ProductosDTO datosActualizados){
        log.info("Actualizando producto con ID: {}", id);
        log.debug("Nuevos valores - Nombre: {}, Precio: {}, Stock: {}",
                datosActualizados.getNombre(),
                datosActualizados.getPrecio(),
                datosActualizados.getStock()
        );

        Productos existente = productosRepository.findById(id).orElseThrow(
                () -> {log.warn("Intento de actualizar producto inexistente con ID: {}", id);
                        return new EntityNotFoundException("Producto no encontrado con ID: " + id);});

        //Valida stock si se envia un nuevo producto
        if(datosActualizados.getStock() != null){
            validarStockPositivo(datosActualizados.getStock());
            existente.setStock(datosActualizados.getStock());
        }
        if(datosActualizados.getNombre() != null){
            if (datosActualizados.getNombre().isBlank()) {
                throw new IllegalArgumentException(
                        "El nombre del producto no puede estar vacío");
            }

            existente.setNombre(datosActualizados.getNombre());
        }
        if(datosActualizados.getDescripcion() != null){
            existente.setDescripcion(datosActualizados.getDescripcion());
        }
        if(datosActualizados.getPrecio() != null){
            if (datosActualizados.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "El precio debe ser mayor a cero");
            }

            existente.setPrecio(datosActualizados.getPrecio());
        }

        Productos updated = productosRepository.save(existente);

        log.info("Producto actualizado exitosamente - ID: {}, Nuevo precio: {}, Nuevo stock: {}",
                updated.getId(),
                updated.getPrecio(),
                updated.getStock()
        );

        return mapToDTO(updated);
    }

    public ProductosDTO obtenerProductoPorId(UUID id){
        log.debug("Buscando producto por ID: {}", id);
        Productos entity = productosRepository.findById(id).orElseThrow(
                () -> {log.warn("Producto no encontrado con ID: {}", id);
                        return new EntityNotFoundException("Producto no encontrado con ID: " + id);});
        return mapToDTO(entity);
    }

    //eliminar producto
    public void eliminarProducto(UUID id) {
        log.info("Intentando eliminar producto con ID: {}", id);
        if (!productosRepository.existsById(id)) {
            log.warn("Intento de eliminar producto inexistente con ID: {}", id);
            throw new EntityNotFoundException("Producto no encontrado con ID: " + id);
        }
        productosRepository.deleteById(id);
        log.info("Producto eliminado exitosamente: {}", id);
    }

                                //Metodos del stock
    //Consultar el stock del prodcuto
    public Integer consultarStock(UUID id){
        log.debug("Consultando stock del producto: {}", id);
        Integer stock = productosRepository.findStockById(id);
        if(stock == null){
            log.warn("Producto no encontrado al consultar stock: {}", id);
            throw new EntityNotFoundException("Producto no encontrado con ID: " + id);
        }
        log.debug("Stock actual del producto {}: {}", id, stock);
        return stock;
    }

    //validar suficiente sotck para una cantidad
    public boolean stockSuficiente(UUID id, int cantidadRequerida){
        Integer stock = consultarStock(id);
        boolean suficiente = stock >= cantidadRequerida;
        if (!suficiente) {
            log.warn("Stock insuficiente para producto {}: requerido {}, disponible {}", id, cantidadRequerida, stock);
        }
        return suficiente;
    }

    //Decrementar stock, retorna verdadero si se puede decrementar, falso si no hay suficiente stock
    @Transactional
    public boolean decrementarStock(UUID id, int cantidad){
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        log.debug("Intentando decrementar stock de producto {} en {} unidades", id, cantidad);
        int filasActualizadas = productosRepository.decrementarStock(id, cantidad);
        if (filasActualizadas > 0) {
            log.info("Stock decrementado exitosamente para producto {}", id);
        } else {
            log.warn("Intento fallido de decrementar stock: stock insuficiente para producto {}", id);
        }
        return filasActualizadas > 0;
    }

    //incrementar stock, cuando se elimina el carrito
    @Transactional
    public void incrementarStock(UUID id, int cantidad){
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        int filasActualizadas = productosRepository.incrementarStock(id, cantidad);
        if (filasActualizadas == 0) {
            throw new EntityNotFoundException("Producto no encontrado con ID: " + id);
        }
    }
}
