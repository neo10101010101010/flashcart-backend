package com.flashcart.flashcart_backend.Controllers;

import com.flashcart.flashcart_backend.DTOS.ProductosDTO;
import com.flashcart.flashcart_backend.services.ProductosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Productos", description = "Operaciones relacionadas con productos")
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/productos")
public class ProductosController {
    private final ProductosService productosService;

    public ProductosController(ProductosService productosService){
        this.productosService = productosService;
    }

    @Operation(
            summary = "Listar todos los productos",
            description = "Obtiene la lista completa de productos registrados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de productos obtenida correctamente"
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No existen productos registrados"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    @GetMapping
    public ResponseEntity<List<ProductosDTO>> listarTodos() {
        return ResponseEntity.ok(productosService.listarTodos());
    }

    @Operation(
            summary = "Listar todos los productos disponibles",
            description = "Obtiene la lista completa de productos disponibles"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de productos obtenida correctamente"
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No existen productos disponibles"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    @GetMapping("/disponibles")
    public ResponseEntity<List<ProductosDTO>> listarDisponibles() {
        return ResponseEntity.ok(productosService.listarDisponibles());
    }

    @Operation(
            summary = "mostrar producto por id",
            description = "Obtiene los datos de un producto por su id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "producto obtenido correctamente"
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No existe productos"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductosDTO> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(productosService.obtenerProductoPorId(id));
    }

    @Operation(
            summary = "mostrar producto por id y su stock",
            description = "Obtiene los datos de un producto por su id y su stock disponoble"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "producto obtenido correctamente"
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No existe productos"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    @GetMapping("/{id}/stock")
    public ResponseEntity<Integer> consultarStock(@PathVariable UUID id) {
        return ResponseEntity.ok(productosService.consultarStock(id));
    }

    @Operation(
            summary = "Crea producto",
            description = "Crea los datos de un producto nuevo"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "producto creado correctamente"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    @PostMapping
    public ResponseEntity<ProductosDTO> crearProducto(@Valid @RequestBody ProductosDTO dto) {
        ProductosDTO nuevo = productosService.crearProducto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @Operation(
            summary = "actualiza producto",
            description = "actualiza los datos de un producto existente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "producto actualizado correctamente"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductosDTO> actualizarProducto(
            @PathVariable UUID id,
            @Valid @RequestBody ProductosDTO dto) {
        ProductosDTO actualizado = productosService.actualizarProductos(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @Operation(
            summary = "elimina producto",
            description = "elimina producto de la lista"
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable UUID id) {
        productosService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
