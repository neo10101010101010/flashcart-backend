package com.flashcart.flashcart_backend;

import com.flashcart.flashcart_backend.DTOS.AgregarProductoCarritoDTO;
import com.flashcart.flashcart_backend.DTOS.CarritoDTO;
import com.flashcart.flashcart_backend.DTOS.CarritoItemsDTO;
import com.flashcart.flashcart_backend.Enums.EstadoCarrito;
import com.flashcart.flashcart_backend.models.Carrito;
import com.flashcart.flashcart_backend.models.CarritoItems;
import com.flashcart.flashcart_backend.models.Productos;
import com.flashcart.flashcart_backend.models.Usuario;
import com.flashcart.flashcart_backend.repositories.CarritoItemRepository;
import com.flashcart.flashcart_backend.repositories.CarritoRepository;
import com.flashcart.flashcart_backend.repositories.ProductosRepository;
import com.flashcart.flashcart_backend.repositories.UsuarioRepository;
import com.flashcart.flashcart_backend.services.CarritoService;
import com.flashcart.flashcart_backend.services.ProductosService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios para service del carrito")
public class CarritoServiceTests {
    @Mock
    private CarritoRepository carritoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ProductosService productosService;
    @Mock
    private CarritoItemRepository carritoItemRepository;
    @Mock
    private ProductosRepository productosRepository;

    @InjectMocks
    private CarritoService carritoService;

    private Long usuarioId;
    private UUID productoId;
    private Usuario usuario;
    private Productos producto;
    private Carrito carrito;
    private CarritoItems carritoItem;

    @BeforeEach
    void septup(){
        usuarioId = 1L;
        productoId = UUID.randomUUID();

        usuario = new Usuario();
        usuario.setId(usuarioId);

        producto = new Productos();
        producto.setId(productoId);
        producto.setPrecio(new BigDecimal("100.00"));
        producto.setStock(20);

        carrito = new Carrito();
        carrito.setId(1L);
        carrito.setUsuario(usuario);
    }

    @Test
    @DisplayName("Agregar producto correctamente al carrito")
    void agregarProducto_Correctamente() {
        Integer cantidad = 2;

        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuario));

        when(carritoRepository.findByUsuarioIdAndEstado(
                usuarioId,
                EstadoCarrito.ACTIVO))
                .thenReturn(Optional.of(carrito));

        when(productosRepository.findById(productoId))
                .thenReturn(Optional.of(producto));

        when(carritoItemRepository.findByCarritoIdAndProductoId(
                carrito.getId(),
                productoId))
                .thenReturn(Optional.empty());

        carritoService.agregarProducto(
                usuarioId,
                productoId,
                cantidad);

        verify(productosService).stockSuficiente(productoId, cantidad);
        verify(productosService).decrementarStock(productoId, cantidad);
        verify(carritoItemRepository).save(any(CarritoItems.class));
    }

    @Test
    @DisplayName("Agregar producto con cantidad inválida lanza excepción")
    void agregarProducto_CantidadInvalida_LanzaExcepcion() {
        Integer cantidad = 0;

        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuario));

        when(carritoRepository.findByUsuarioIdAndEstado(
                usuarioId,
                EstadoCarrito.ACTIVO))
                .thenReturn(Optional.of(carrito));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> carritoService.agregarProducto(
                        usuarioId,
                        productoId,
                        cantidad)
        );

        assertEquals(
                "La cantidad debe ser mayor que cero.",
                exception.getMessage()
        );

        verify(productosRepository, never()).findById(any());
        verify(productosService, never()).stockSuficiente(any(UUID.class), anyInt());
        verify(productosService, never()).decrementarStock(any(UUID.class), anyInt());
        verify(carritoItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Agregar producto inexistente lanza excepción")
    void agregarProducto_ProductoNoExiste_LanzaExcepcion() {
        Integer cantidad = 2;

        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuario));
        when(carritoRepository.findByUsuarioIdAndEstado(
                usuarioId,
                EstadoCarrito.ACTIVO))
                .thenReturn(Optional.of(carrito));
        when(productosRepository.findById(productoId))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> carritoService.agregarProducto(
                        usuarioId,
                        productoId,
                        cantidad)
        );

        assertEquals(
                "Producto no encontrado.",
                exception.getMessage()
        );

        verify(productosService, never()).stockSuficiente(any(UUID.class), anyInt());
        verify(productosService, never()).decrementarStock(any(UUID.class), anyInt());
        verify(carritoItemRepository, never()).save(any(CarritoItems.class));
    }

    @Test
    @DisplayName("Agregar un producto existente incrementa la cantidad")
    void agregarProducto_ProductoYaExiste_ActualizaCantidad() {
        Integer cantidad = 3;

        carritoItem = new CarritoItems();
        carritoItem.setCarrito(carrito);
        carritoItem.setProducto(producto);
        carritoItem.setCantidad(2);
        carritoItem.setPrecioUnitario(producto.getPrecio());

        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuario));
        when(carritoRepository.findByUsuarioIdAndEstado(
                usuarioId,
                EstadoCarrito.ACTIVO))
                .thenReturn(Optional.of(carrito));
        when(productosRepository.findById(productoId))
                .thenReturn(Optional.of(producto));
        when(carritoItemRepository.findByCarritoIdAndProductoId(
                carrito.getId(),
                productoId))
                .thenReturn(Optional.of(carritoItem));

        carritoService.agregarProducto(
                usuarioId,
                productoId,
                cantidad);

        assertEquals(5, carritoItem.getCantidad());
        verify(productosService).stockSuficiente(productoId, cantidad);
        verify(productosService).decrementarStock(productoId, cantidad);
        verify(carritoItemRepository).save(carritoItem);
    }

    @Test
    @DisplayName("Eliminar producto correctamente del carrito")
    void eliminarProducto_Correctamente() {
        carritoItem = new CarritoItems();
        carritoItem.setCarrito(carrito);
        carritoItem.setProducto(producto);
        carritoItem.setCantidad(3);

        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuario));

        when(carritoRepository.findByUsuarioIdAndEstado(
                usuarioId,
                EstadoCarrito.ACTIVO))
                .thenReturn(Optional.of(carrito));

        when(carritoItemRepository.findByCarritoIdAndProductoId(
                carrito.getId(),
                productoId))
                .thenReturn(Optional.of(carritoItem));

        carritoService.eliminarProducto(usuarioId, productoId);

        verify(productosService).incrementarStock(productoId, 3);
        verify(carritoItemRepository).delete(carritoItem);
    }

    @Test
    @DisplayName("Visualizar carrito correctamente")
    void visualizarCarrito_Correctamente() {
        carritoItem = new CarritoItems();
        carritoItem.setId(1L);
        carritoItem.setCarrito(carrito);
        carritoItem.setProducto(producto);
        carritoItem.setCantidad(2);
        carritoItem.setPrecioUnitario(new BigDecimal("100.00"));

        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuario));

        when(carritoRepository.findByUsuarioIdAndEstado(
                usuarioId,
                EstadoCarrito.ACTIVO))
                .thenReturn(Optional.of(carrito));

        when(carritoItemRepository.findByCarritoId(carrito.getId()))
                .thenReturn(List.of(carritoItem));

        CarritoDTO resultado = carritoService.visualizarCarrito(usuarioId);

        assertNotNull(resultado);

        assertEquals(carrito.getId(), resultado.getId());
        assertEquals(usuarioId, resultado.getUsuario_id());
        assertEquals(EstadoCarrito.ACTIVO, resultado.getEstado());

        assertEquals(new BigDecimal("200.00"), resultado.getSubtotal());
        assertEquals(new BigDecimal("32.0000"), resultado.getIva());
        assertEquals(new BigDecimal("232.0000"), resultado.getTotal());

        assertEquals(2, resultado.getCantidadArticulos());
        assertEquals(1, resultado.getItems().size());

        CarritoItemsDTO itemDTO = resultado.getItems().get(0);

        assertEquals(carritoItem.getId(), itemDTO.getId());
        assertEquals(carrito.getId(), itemDTO.getCarrito_id());
        assertEquals(productoId, itemDTO.getProducto_id());
        assertEquals(2, itemDTO.getCantidad());
        assertEquals(new BigDecimal("100.00"), itemDTO.getPrecioUnitario());
    }

    @Test
    @DisplayName("Procesar carrito correctamente")
    void procesarCarrito_Correctamente() {
        carrito.setEstado(EstadoCarrito.ACTIVO);

        carritoItem = new CarritoItems();
        carritoItem.setId(1L);
        carritoItem.setCarrito(carrito);
        carritoItem.setProducto(producto);
        carritoItem.setCantidad(2);
        carritoItem.setPrecioUnitario(new BigDecimal("100.00"));

        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuario));

        when(carritoRepository.findByUsuarioIdAndEstado(
                usuarioId,
                EstadoCarrito.ACTIVO))
                .thenReturn(Optional.of(carrito));

        when(carritoItemRepository.findByCarritoId(carrito.getId()))
                .thenReturn(List.of(carritoItem));

        when(carritoRepository.save(any(Carrito.class)))
                .thenReturn(carrito);

        CarritoDTO resultado = carritoService.procesarCarrito(usuarioId);

        assertNotNull(resultado);

        assertEquals(EstadoCarrito.PROCESADO, resultado.getEstado());

        assertEquals(new BigDecimal("200.00"), resultado.getSubtotal());
        assertEquals(new BigDecimal("32.0000"), resultado.getIva());
        assertEquals(new BigDecimal("232.0000"), resultado.getTotal());
        assertEquals(2, resultado.getCantidadArticulos());
        assertEquals(1, resultado.getItems().size());

        CarritoItemsDTO item = resultado.getItems().get(0);

        assertEquals(carritoItem.getId(), item.getId());
        assertEquals(carrito.getId(), item.getCarrito_id());
        assertEquals(productoId, item.getProducto_id());
        assertEquals(2, item.getCantidad());
        assertEquals(new BigDecimal("100.00"), item.getPrecioUnitario());

        verify(productosService).stockSuficiente(productoId, 2);

        ArgumentCaptor<Carrito> captor = ArgumentCaptor.forClass(Carrito.class);
        verify(carritoRepository).save(captor.capture());
        Carrito carritoGuardado = captor.getValue();

        assertEquals(
                EstadoCarrito.PROCESADO,
                carritoGuardado.getEstado()
        );
    }
}
