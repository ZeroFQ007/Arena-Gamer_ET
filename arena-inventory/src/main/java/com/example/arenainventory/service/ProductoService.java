package com.example.arenainventory.service;

import com.example.arenainventory.dto.ProductoCommand;
import com.example.arenainventory.dto.ProductoResult;
import com.example.arenainventory.exception.ProductoNotFoundException;
import com.example.arenainventory.model.Producto;
import com.example.arenainventory.repository.ProductoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<ProductoResult> listarTodos() {
        List<ProductoResult> productos = productoRepository.findAll().stream()
                .map(this::toResult).toList();
        log.info("Listando todos los productos: {} registros", productos.size());
        return productos;
    }

    public List<ProductoResult> listarPorCategoria(String categoria) {
        try {
            Producto.Categoria cat = Producto.Categoria.valueOf(categoria.toUpperCase());
            List<ProductoResult> productos = productoRepository.findByCategoria(cat).stream()
                    .map(this::toResult).toList();
            log.info("Productos filtrados por categoria {}: {}", cat, productos.size());
            return productos;
        } catch (IllegalArgumentException e) {
            log.warn("Categoría inválida ingresada: {}", categoria);
            throw new IllegalArgumentException(
                    "Categoría inválida. Use: CONSOLA, PERIFERICO o JUEGO");
        }
    }

    public ProductoResult obtenerPorId(Long id) {
        ProductoResult resultado = toResult(buscarOFallar(id));
        log.info("Producto obtenido: id={}", id);
        return resultado;
    }

    public ProductoResult crear(ProductoCommand cmd) {
        if (cmd.stock() != null && cmd.stock() < 0) {
            log.warn("Intento de crear producto con stock negativo");
            throw new IllegalArgumentException(
                    "El stock inicial no puede ser negativo");
        }
        Producto producto = new Producto();
        producto.setNombre(cmd.nombre());
        producto.setCategoria(cmd.categoria());
        producto.setStock(cmd.stock());
        producto.setPrecioAlquiler(cmd.precioAlquiler());
        ProductoResult resultado = toResult(productoRepository.save(producto));
        log.info("Producto creado: id={}, nombre={}", resultado.id(), resultado.nombre());
        return resultado;
    }

    public ProductoResult actualizar(Long id, ProductoCommand cmd) {
        Producto existente = buscarOFallar(id);
        existente.setNombre(cmd.nombre());
        existente.setCategoria(cmd.categoria());
        existente.setStock(cmd.stock());
        existente.setPrecioAlquiler(cmd.precioAlquiler());
        ProductoResult resultado = toResult(productoRepository.save(existente));
        log.info("Producto actualizado: id={}, nombre={}", id, resultado.nombre());
        return resultado;
    }

    public ProductoResult actualizarStock(Long id, Integer cantidad) {
        if (cantidad == null || cantidad == 0) {
            log.warn("Intento de actualizar stock con cantidad inválida: {}", cantidad);
            throw new IllegalArgumentException(
                    "La cantidad debe ser distinta de cero");
        }
        Producto producto = buscarOFallar(id);
        int nuevoStock = producto.getStock() + cantidad;

        if (nuevoStock < 0) {
            log.warn("Stock insuficiente para producto id={}: disponible={}, requerido={}",
                    id, producto.getStock(), -cantidad);
            throw new IllegalArgumentException(
                    "Stock insuficiente. Disponible: " + producto.getStock());
        }
        producto.setStock(nuevoStock);
        ProductoResult resultado = toResult(productoRepository.save(producto));
        log.info("Stock actualizado: producto id={}, nuevo stock={}", id, nuevoStock);
        return resultado;
    }

    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            log.warn("Intento de eliminar producto inexistente: id={}", id);
            throw new ProductoNotFoundException(
                    "No se puede eliminar: ID " + id + " no existe");
        }
        productoRepository.deleteById(id);
        log.info("Producto eliminado: id={}", id);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private Producto buscarOFallar(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(
                        "Producto con ID " + id + " no encontrado"));
    }

    private ProductoResult toResult(Producto p) {
        return new ProductoResult(
                p.getId(), p.getNombre(), p.getCategoria(),
                p.getStock(), p.getPrecioAlquiler());
    }
}