package com.flashcart.flashcart_backend.services;

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
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CarritoService {
    private static final Logger log = LoggerFactory.getLogger(CarritoService.class);

    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductosService productosService;
    private final ProductosRepository productosRepository;
    private final CarritoItemRepository carritoItemRepository;

    public CarritoService(CarritoRepository carritoRepository, UsuarioRepository usuarioRepository,
    ProductosService productosService, CarritoItemRepository carritoItemRepository, ProductosRepository productosRepository){
        this.carritoRepository = carritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productosService = productosService;
        this.carritoItemRepository = carritoItemRepository;
        this.productosRepository = productosRepository;
    }

    private Carrito obtenerCarrito(Long usuarioId){
        log.info("Obteniendo carrito activo para el usuario: {}", usuarioId);
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(
                () -> {
                    log.warn("Usuario no encontrado. ID: {}", usuarioId);
                    return new EntityNotFoundException("Usuario no encontrado.");
                }
        );

        return carritoRepository.findByUsuarioIdAndEstado(usuarioId, EstadoCarrito.ACTIVO).orElseGet(() -> {
            Carrito carrito = new Carrito();
            carrito.setUsuario(usuario);
            carrito.setEstado(EstadoCarrito.ACTIVO);
            log.info("No existe carrito activo. Creando uno nuevo para el usuario: {}", usuarioId);
            return carritoRepository.save(carrito);
        });
    }

    public void agregarProducto(Long usuarioId, UUID productoId, Integer cantidad){
        log.info("Agregando producto {} al carrito del usuario {}",
                productoId,
                usuarioId);

        Carrito carrito = obtenerCarrito(usuarioId);

        if (cantidad == null || cantidad <= 0) {
            log.warn("Cantidad inválida ({}) para el producto {}",
                    cantidad,
                    productoId);
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }

        Productos producto = productosRepository.findById(productoId)
                .orElseThrow(() ->{
                    log.warn("Producto no encontrado. ID: {}", productoId);
                    return new EntityNotFoundException("Producto no encontrado.");
                });

        productosService.stockSuficiente(productoId, cantidad);

        Optional<CarritoItems> carritoItemExistente =
                carritoItemRepository.findByCarritoIdAndProductoId(
                        carrito.getId(),
                        productoId
                );

        CarritoItems carritoItem;

        if (carritoItemExistente.isPresent()) {
            carritoItem = carritoItemExistente.get();
            carritoItem.setCantidad(carritoItem.getCantidad() + cantidad);

        } else {
            log.debug("Producto agregado por primera vez al carrito.");
            carritoItem = new CarritoItems();
            carritoItem.setCarrito(carrito);
            carritoItem.setProducto(producto);
            carritoItem.setCantidad(cantidad);
            carritoItem.setPrecioUnitario(producto.getPrecio());
        }

        productosService.decrementarStock(productoId, cantidad);

        log.info("Producto {} agregado correctamente al carrito {}",
                productoId,
                carrito.getId());
        carritoItemRepository.save(carritoItem);
        /*
        inventarioLogService.registrarSalida(
                productoId,
                cantidad,
                "Producto agregado al carrito"
        );
         */
    }

    public void eliminarProducto(Long usuarioId, UUID productoId) {
        log.info("Eliminando producto {} del carrito del usuario {}",
                productoId,
                usuarioId);

        Carrito carrito = obtenerCarrito(usuarioId);

        CarritoItems carritoItem = carritoItemRepository
                .findByCarritoIdAndProductoId(carrito.getId(), productoId)
                .orElseThrow(() ->{
                    log.warn("Intento de eliminar un producto inexistente del carrito.");
                    return new EntityNotFoundException("El producto no existe en el carrito.");
                });

        productosService.incrementarStock(
                productoId,
                carritoItem.getCantidad()
        );

        log.info("Producto {} eliminado correctamente del carrito.",
                productoId);

        carritoItemRepository.delete(carritoItem);
    }

    private BigDecimal calcularSubtotal(List<CarritoItems> items) {

        return items.stream()
                .map(item -> item.getPrecioUnitario()
                        .multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static final BigDecimal IVA = new BigDecimal("0.16");
    private BigDecimal calcularIVA(BigDecimal subtotal) {
        return subtotal.multiply(IVA);
    }

    private BigDecimal calcularTotal(BigDecimal subtotal,
                                     BigDecimal iva) {
        return subtotal.add(iva);
    }

    private CarritoDTO construirCarritoDTO(Carrito carrito,
                                           List<CarritoItems> items){

        BigDecimal subtotal = calcularSubtotal(items);

        BigDecimal iva = calcularIVA(subtotal);

        BigDecimal total = calcularTotal(subtotal, iva);

        CarritoDTO dto = new CarritoDTO();

        dto.setId(carrito.getId());
        dto.setUsuario_id(carrito.getUsuario().getId());
        dto.setEstado(carrito.getEstado());

        dto.setSubtotal(subtotal);
        dto.setIva(iva);
        dto.setTotal(total);

        dto.setCantidadArticulos(calcularCantidadArticulos(items));

        dto.setItems(convertirItemsDTO(items));

        return dto;
    }

    public CarritoDTO visualizarCarrito(Long usuarioId){
        log.info("Consultando carrito del usuario {}",
                usuarioId);

        Carrito carrito = obtenerCarrito(usuarioId);

        List<CarritoItems> items =
                carritoItemRepository.findByCarritoId(carrito.getId());

        log.debug("El carrito contiene {} productos.",
                items.size());

        log.info("Carrito {} consultado correctamente.",
                carrito.getId());

        return construirCarritoDTO(carrito, items);
    }

    private List<CarritoItemsDTO> convertirItemsDTO(List<CarritoItems> items){

        List<CarritoItemsDTO> itemsDTO = new ArrayList<>();

        for (CarritoItems item : items){

            CarritoItemsDTO dto = new CarritoItemsDTO();

            dto.setId(item.getId());
            dto.setCarrito_id(item.getCarrito().getId());
            dto.setProducto_id(item.getProducto().getId());
            dto.setCantidad(item.getCantidad());
            dto.setPrecioUnitario(item.getPrecioUnitario());

            itemsDTO.add(dto);
        }

        return itemsDTO;
    }

    private Integer calcularCantidadArticulos(List<CarritoItems> items) {

        return items.stream()
                .mapToInt(CarritoItems::getCantidad)
                .sum();
    }

    public CarritoDTO procesarCarrito(Long usuarioId) {
        log.info("Procesando compra del usuario {}",
                usuarioId);

        Carrito carrito = obtenerCarrito(usuarioId);

        List<CarritoItems> items =
                carritoItemRepository.findByCarritoId(carrito.getId());

        if (items.isEmpty()) {
            log.warn("Intento de procesar un carrito vacío. Usuario {}",
                    usuarioId);
            throw new IllegalArgumentException("El carrito está vacío.");
        }

        log.debug("Validando disponibilidad de {} productos.",
                items.size());
        for (CarritoItems item : items) {
            productosService.stockSuficiente(
                    item.getProducto().getId(),
                    item.getCantidad()
            );
        }

        carrito.setEstado(EstadoCarrito.PROCESADO);
        log.info("Carrito {} procesado correctamente.",
                carrito.getId());
        carritoRepository.save(carrito);

        return construirCarritoDTO(carrito, items);
    }
}
