package com.flashcart.flashcart_backend;

import com.flashcart.flashcart_backend.DTOS.ProductosDTO;
import com.flashcart.flashcart_backend.models.Productos;
import com.flashcart.flashcart_backend.repositories.ProductosRepository;
import com.flashcart.flashcart_backend.services.ProductosService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("Test unitarios para productos services")
public class ProductoServiceTest {
    @Mock
    private ProductosRepository productosRepository;

    @InjectMocks
    private ProductosService productosService;

    private UUID productId;
    private Productos productoEntity;
    private ProductosDTO productosDTO;

    @BeforeEach
    void setup(){
        productId = UUID.randomUUID();

        productoEntity = new Productos();
        productoEntity.setId(productId);
        productoEntity.setNombre("Laptop Gamer");
        productoEntity.setDescripcion("RTX 4080, 32GB RAM");
        productoEntity.setPrecio(new BigDecimal("2499.99"));
        productoEntity.setStock(10);

        productosDTO = new ProductosDTO(
                UUID.fromString("3988bea5-9730-4bcf-bf91-43371c2a3498"),
                "Laptop Gamer",
                "RTX 4080, 32GB RAM",
                new BigDecimal("2499.99"),
                10
        );
    }

    //Test de crear productos
    @Test
    @DisplayName("Crear producto exitosamente")
    void crearProducto_Exitoso() {
        when(productosRepository.save(any(Productos.class))).thenReturn(productoEntity);

        ProductosDTO resultado = productosService.crearProducto(productosDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Laptop Gamer");
        assertThat(resultado.getPrecio()).isEqualTo(new BigDecimal("2499.99"));
        verify(productosRepository, times(1)).save(any(Productos.class));
    }

    @Test
    @DisplayName("Crear producto con stock negativo lanza excepción")
    void crearProducto_StockNegativo_LanzaExcepcion() {
        productosDTO.setStock(-5);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> productosService.crearProducto(productosDTO));

        System.out.println("Mensaje real: " + exception.getMessage());
        assertThat(exception.getMessage()).isEqualTo("El stock no puede ser negativo");
    }

    //Tests de actualizar producto
    @Test
    @DisplayName("Actualizar producto exitosamente")
    void actualizarProducto_Exitoso() {
        ProductosDTO updateDTO = new ProductosDTO();
        updateDTO.setPrecio(new BigDecimal("1999.99"));
        updateDTO.setStock(5);

        when(productosRepository.findById(productId)).thenReturn(Optional.of(productoEntity));
        when(productosRepository.save(any(Productos.class))).thenReturn(productoEntity);

        ProductosDTO resultado = productosService.actualizarProductos(productId, updateDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getPrecio()).isEqualTo(new BigDecimal("1999.99"));
        assertThat(resultado.getStock()).isEqualTo(5);
        verify(productosRepository, times(1)).save(any(Productos.class));
    }

    @Test
    @DisplayName("Actualizar producto con stock negativo lanza excepción")
    void actualizarProducto_StockNegativo_LanzaExcepcion() {
        ProductosDTO updateDTO = new ProductosDTO();
        updateDTO.setStock(-1);
        System.out.println(updateDTO.getStock());

        when(productosRepository.findById(productId)).thenReturn(Optional.of(productoEntity));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> productosService.actualizarProductos(productId, updateDTO));

        assertThat(exception.getMessage()).contains("El stock no puede ser negativo");
        verify(productosRepository, never()).save(any(Productos.class));
    }

    //Test para eliminar productos
    @Test
    @DisplayName("Eliminar producto existente")
    void eliminarProducto_Existente_EliminaCorrectamente() {
        when(productosRepository.existsById(productId)).thenReturn(true);

        productosService.eliminarProducto(productId);

        verify(productosRepository, times(1)).deleteById(productId);
    }

    @Test
    @DisplayName("Eliminar producto inexistente lanza EntityNotFoundException")
    void eliminarProducto_Inexistente_LanzaExcepcion() {
        UUID idInexistente = UUID.randomUUID();
        when(productosRepository.existsById(idInexistente)).thenReturn(false);

        assertThatThrownBy(() -> productosService.eliminarProducto(idInexistente))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Producto no encontrado con ID: " + idInexistente);

        verify(productosRepository, never()).deleteById(any(UUID.class));
    }

    //Test de stock
    @Test
    @DisplayName("Consultar stock de producto existente")
    void consultarStock_Existente_RetornaCantidad() {
        when(productosRepository.findStockById(productId)).thenReturn(10);

        Integer stock = productosService.consultarStock(productId);

        assertThat(stock).isEqualTo(10);
        verify(productosRepository, times(1)).findStockById(productId);
    }

    @Test
    @DisplayName("Consultar stock de producto inexistente lanza excepción")
    void consultarStock_Inexistente_LanzaExcepcion() {
        UUID idInexistente = UUID.randomUUID();
        when(productosRepository.findStockById(idInexistente)).thenReturn(null);

        assertThatThrownBy(() -> productosService.consultarStock(idInexistente))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Producto no encontrado con ID: " + idInexistente);
    }

    @Test
    @DisplayName("Verificar stock suficiente - verdadero")
    void stockSuficiente_ConStock_RetornaTrue() {
        when(productosRepository.findStockById(productId)).thenReturn(10);

        boolean suficiente = productosService.stockSuficiente(productId, 5);

        assertThat(suficiente).isTrue();
    }

    @Test
    @DisplayName("Verificar stock insuficiente - falso")
    void stockSuficiente_SinStock_RetornaFalse() {
        when(productosRepository.findStockById(productId)).thenReturn(3);

        boolean suficiente = productosService.stockSuficiente(productId, 5);

        assertThat(suficiente).isFalse();
    }

    @Test
    @DisplayName("Decrementar stock exitosamente")
    void decrementarStock_Exitoso_RetornaTrue() {
        when(productosRepository.decrementarStock(productId, 3)).thenReturn(1);

        boolean resultado = productosService.decrementarStock(productId, 3);

        assertThat(resultado).isTrue();
        verify(productosRepository, times(1)).decrementarStock(productId, 3);
    }

    @Test
    @DisplayName("Decrementar stock insuficiente retorna false")
    void decrementarStock_Insuficiente_RetornaFalse() {
        when(productosRepository.decrementarStock(productId, 3)).thenReturn(0);

        boolean resultado = productosService.decrementarStock(productId, 3);

        assertThat(resultado).isFalse();
    }

    @Test
    @DisplayName("Incrementar stock exitosamente")
    void incrementarStock_Exitoso() {
        when(productosRepository.incrementarStock(productId, 5)).thenReturn(1);

        productosService.incrementarStock(productId, 5);

        verify(productosRepository, times(1)).incrementarStock(productId, 5);
    }

    @Test
    @DisplayName("Incrementar stock de producto inexistente lanza excepción")
    void incrementarStock_Inexistente_LanzaExcepcion() {
        UUID idInexistente = UUID.randomUUID();
        when(productosRepository.incrementarStock(idInexistente, 5)).thenReturn(0);

        assertThatThrownBy(() -> productosService.incrementarStock(idInexistente, 5))
                .isInstanceOf(EntityNotFoundException.class)
                .satisfies(ex -> log.info("Mensaje de la excepción: {}", ex.getMessage()));
    }

    @Test
    @DisplayName("Incrementar stock con cantidad negativa lanza excepción")
    void incrementarStock_CantidadNegativa_LanzaExcepcion() {
        assertThatThrownBy(() -> productosService.incrementarStock(productId, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .satisfies(ex -> log.info("Mensaje de la excepción: {}", ex.getMessage()));
    }
}
