package com.example.arenainventory.service;

import com.example.arenainventory.dto.ProductoCommand;
import com.example.arenainventory.dto.ProductoResult;
import com.example.arenainventory.exception.ProductoNotFoundException;
import com.example.arenainventory.model.Producto;
import com.example.arenainventory.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void listarTodos_ReturnsAll() {
        when(productoRepository.findAll()).thenReturn(List.of(new Producto(), new Producto()));

        List<ProductoResult> result = productoService.listarTodos();

        assertEquals(2, result.size());
    }

    @Test
    void listarPorCategoria_ValidCategory_ReturnsFiltered() {
        Producto p = new Producto(1L, "PS5", Producto.Categoria.CONSOLA, 5, 2500.0);
        when(productoRepository.findByCategoria(Producto.Categoria.CONSOLA))
                .thenReturn(List.of(p));

        List<ProductoResult> result = productoService.listarPorCategoria("CONSOLA");

        assertEquals(1, result.size());
        assertEquals("PS5", result.get(0).nombre());
    }

    @Test
    void listarPorCategoria_InvalidCategory_Throws() {
        assertThrows(IllegalArgumentException.class,
                () -> productoService.listarPorCategoria("INVALIDA"));
    }

    @Test
    void obtenerPorId_WhenExists_Returns() {
        Producto p = new Producto(1L, "PS5", Producto.Categoria.CONSOLA, 5, 2500.0);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(p));

        ProductoResult result = productoService.obtenerPorId(1L);

        assertEquals(1L, result.id());
        assertEquals("PS5", result.nombre());
    }

    @Test
    void obtenerPorId_WhenNotExists_Throws() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductoNotFoundException.class,
                () -> productoService.obtenerPorId(99L));
    }

    @Test
    void crear_WithValidData_Saves() {
        ProductoCommand cmd = new ProductoCommand("PS5", Producto.Categoria.CONSOLA, 5, 2500.0);
        Producto saved = new Producto(1L, "PS5", Producto.Categoria.CONSOLA, 5, 2500.0);
        when(productoRepository.save(any(Producto.class))).thenReturn(saved);

        ProductoResult result = productoService.crear(cmd);

        assertEquals(1L, result.id());
        assertEquals("PS5", result.nombre());
    }

    @Test
    void crear_WithNegativeStock_Throws() {
        ProductoCommand cmd = new ProductoCommand("PS5", Producto.Categoria.CONSOLA, -1, 2500.0);

        assertThrows(IllegalArgumentException.class,
                () -> productoService.crear(cmd));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void actualizar_WhenExists_Updates() {
        Producto existing = new Producto(1L, "PS5", Producto.Categoria.CONSOLA, 5, 2500.0);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(existing));

        ProductoCommand cmd = new ProductoCommand("PS5 Pro", Producto.Categoria.CONSOLA, 3, 3500.0);
        when(productoRepository.save(any(Producto.class))).thenReturn(existing);

        ProductoResult result = productoService.actualizar(1L, cmd);

        assertEquals("PS5 Pro", result.nombre());
        assertEquals(3, result.stock());
    }

    @Test
    void actualizar_WhenNotExists_Throws() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductoNotFoundException.class,
                () -> productoService.actualizar(99L,
                        new ProductoCommand("Test", Producto.Categoria.JUEGO, 1, 100.0)));
    }

    @Test
    void actualizarStock_WithValidData_Updates() {
        Producto p = new Producto(1L, "PS5", Producto.Categoria.CONSOLA, 5, 2500.0);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(p));

        when(productoRepository.save(any(Producto.class))).thenReturn(p);

        ProductoResult result = productoService.actualizarStock(1L, 3);

        assertEquals(8, result.stock());
    }

    @Test
    void actualizarStock_WithNullCantidad_Throws() {
        assertThrows(IllegalArgumentException.class,
                () -> productoService.actualizarStock(1L, null));
    }

    @Test
    void actualizarStock_WithCeroCantidad_Throws() {
        assertThrows(IllegalArgumentException.class,
                () -> productoService.actualizarStock(1L, 0));
    }

    @Test
    void actualizarStock_WhenInsufficientStock_Throws() {
        Producto p = new Producto(1L, "PS5", Producto.Categoria.CONSOLA, 2, 2500.0);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(p));

        assertThrows(IllegalArgumentException.class,
                () -> productoService.actualizarStock(1L, -5));
    }

    @Test
    void eliminar_WhenExists_Deletes() {
        when(productoRepository.existsById(1L)).thenReturn(true);

        productoService.eliminar(1L);

        verify(productoRepository).deleteById(1L);
    }

    @Test
    void eliminar_WhenNotExists_Throws() {
        when(productoRepository.existsById(99L)).thenReturn(false);

        assertThrows(ProductoNotFoundException.class,
                () -> productoService.eliminar(99L));
        verify(productoRepository, never()).deleteById(any());
    }
}
