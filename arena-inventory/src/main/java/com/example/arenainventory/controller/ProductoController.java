package com.example.arenainventory.controller;

import com.example.arenainventory.dto.ProductoCommand;
import com.example.arenainventory.dto.ProductoRequest;
import com.example.arenainventory.dto.ProductoResponse;
import com.example.arenainventory.dto.ProductoResult;
import com.example.arenainventory.service.ProductoLinkAssembler;
import com.example.arenainventory.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Productos", description = "Operaciones para gestionar el inventario de productos de Arena Gamer")
@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final ProductoLinkAssembler productoLinkAssembler;

    public ProductoController(ProductoService productoService, ProductoLinkAssembler productoLinkAssembler) {
        this.productoService = productoService;
        this.productoLinkAssembler = productoLinkAssembler;
    }

    @Operation(summary = "Listar productos", description = "Obtiene todos los productos con enlaces HATEOAS en _links")
    @ApiResponse(responseCode = "200", description = "Productos obtenidos correctamente")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ProductoResponse>>> listar(
            @Parameter(description = "Categoría a filtrar: CONSOLA, PERIFERICO o JUEGO", example = "CONSOLA")
            @RequestParam(required = false) String categoria) {
        List<ProductoResult> resultado = (categoria != null && !categoria.isBlank())
                ? productoService.listarPorCategoria(categoria)
                : productoService.listarTodos();

        List<EntityModel<ProductoResponse>> productos = resultado.stream()
                .map(this::toResponse)
                .map(productoLinkAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<ProductoResponse>> collection = CollectionModel.of(productos);
        collection.add(linkTo(methodOn(ProductoController.class).listar(null)).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Buscar producto por id", description = "Devuelve el producto con enlaces HATEOAS en _links (self, all, update-stock)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ProductoResponse>> obtenerPorId(
            @Parameter(description = "ID del producto", example = "1")
            @PathVariable Long id) {
        ProductoResponse producto = toResponse(productoService.obtenerPorId(id));
        return ResponseEntity.ok(productoLinkAssembler.toModel(producto));
    }

    @Operation(summary = "Crear producto", description = "Registra un nuevo producto en el inventario")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<ProductoResponse> crear(
            @Valid @RequestBody ProductoRequest request) {
        ProductoCommand cmd = new ProductoCommand(
                request.nombre(), request.categoria(),
                request.stock(), request.precioAlquiler());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(productoService.crear(cmd)));
    }

    @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizar(
            @Parameter(description = "ID del producto", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request) {
        ProductoCommand cmd = new ProductoCommand(
                request.nombre(), request.categoria(),
                request.stock(), request.precioAlquiler());
        return ResponseEntity.ok(toResponse(productoService.actualizar(id, cmd)));
    }

    @Operation(summary = "Actualizar stock", description = "Descuenta o aumenta el stock de un producto (usado por arena-reservas al confirmar)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "El campo 'cantidad' es obligatorio"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductoResponse> actualizarStock(
            @Parameter(description = "ID del producto", example = "1")
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body) {
        if (body == null || !body.containsKey("cantidad")) {
            throw new IllegalArgumentException("El campo 'cantidad' es obligatorio");
        }
        return ResponseEntity.ok(
                toResponse(productoService.actualizarStock(id, body.get("cantidad"))));
    }

    @Operation(summary = "Eliminar producto")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del producto", example = "1")
            @PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private ProductoResponse toResponse(ProductoResult r) {
        return new ProductoResponse(
                r.id(), r.nombre(), r.categoria(),
                r.stock(), r.precioAlquiler());
    }
}