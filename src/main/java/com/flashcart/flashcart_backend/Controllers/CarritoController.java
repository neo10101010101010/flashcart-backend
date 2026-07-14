package com.flashcart.flashcart_backend.Controllers;

import com.flashcart.flashcart_backend.DTOS.AgregarProductoCarritoDTO;
import com.flashcart.flashcart_backend.DTOS.CarritoDTO;
import com.flashcart.flashcart_backend.DTOS.CarritoItemsDTO;
import com.flashcart.flashcart_backend.services.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Carrito", description = "Operaciones relacionadas con carrito")
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/carrito")
public class CarritoController {
    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService){
        this.carritoService = carritoService;
    }

    @Operation(
            summary = "Agregar al carrito",
            description = "agrega productos al carrito de compras"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "carrito creado correctamente"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    @PostMapping("/{usuarioId}/productos")
    public ResponseEntity<String> agregarProducto(
            @PathVariable Long usuarioId,
            @RequestBody AgregarProductoCarritoDTO request) {

        carritoService.agregarProducto(
                usuarioId,
                request.getProductos().getId(),
                request.getCantidad());

        return ResponseEntity.ok("Producto agregado al carrito.");
    }

    @Operation(
            summary = "elimina producto del carrito",
            description = "elimina producto del listado de productos en el carrito"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "producto eliminado correctamente"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    @DeleteMapping("/{usuarioId}/productos/{productoId}")
    public ResponseEntity<String> eliminarProducto(
            @PathVariable Long usuarioId,
            @PathVariable UUID productoId) {

        carritoService.eliminarProducto(usuarioId, productoId);

        return ResponseEntity.ok("Producto eliminado del carrito.");
    }

    @Operation(
            summary = "Vista del carrito",
            description = "Obtiene la lista de productos agregados al carrito"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Vista del carrito"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    @GetMapping("/{usuarioId}")
    public ResponseEntity<CarritoDTO> visualizarCarrito(
            @PathVariable Long usuarioId) {
        System.out.println("Entró al endpoint visualizarCarrito");
        return ResponseEntity.ok(
                carritoService.visualizarCarrito(usuarioId)
        );
    }

    @Operation(
            summary = "Procesar compra",
            description = "Procesa la compra de los productos agregados al carrito"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "procesar venta correctamente"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    @PostMapping("/{usuarioId}/procesar")
    public ResponseEntity<CarritoDTO> procesarCarrito(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                carritoService.procesarCarrito(usuarioId)
        );
    }
}
