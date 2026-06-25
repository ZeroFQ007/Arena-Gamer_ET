package com.example.arenainventory.controller;

import com.example.arenainventory.dto.ProductoCommand;
import com.example.arenainventory.dto.ProductoResponse;
import com.example.arenainventory.dto.ProductoResult;
import com.example.arenainventory.exception.GlobalExceptionHandler;
import com.example.arenainventory.exception.ProductoNotFoundException;
import com.example.arenainventory.model.Producto;
import com.example.arenainventory.service.ProductoLinkAssembler;
import com.example.arenainventory.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
@Import(GlobalExceptionHandler.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @MockBean
    private ProductoLinkAssembler productoLinkAssembler;

    @Test
    void listar_WithoutCategory_Returns200() throws Exception {
        ProductoResult r = new ProductoResult(1L, "PS5", Producto.Categoria.CONSOLA, 5, 2500.0);
        when(productoService.listarTodos()).thenReturn(List.of(r));

        ProductoResponse response = new ProductoResponse(1L, "PS5", Producto.Categoria.CONSOLA, 5, 2500.0);
        when(productoLinkAssembler.toModel(any(ProductoResponse.class))).thenReturn(EntityModel.of(response));

        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.productoResponseList[0].nombre").value("PS5"));
    }

    @Test
    void listar_WithCategory_ReturnsFiltered() throws Exception {
        ProductoResult r = new ProductoResult(1L, "PS5", Producto.Categoria.CONSOLA, 5, 2500.0);
        when(productoService.listarPorCategoria("CONSOLA")).thenReturn(List.of(r));

        ProductoResponse response = new ProductoResponse(1L, "PS5", Producto.Categoria.CONSOLA, 5, 2500.0);
        when(productoLinkAssembler.toModel(any(ProductoResponse.class))).thenReturn(EntityModel.of(response));

        mockMvc.perform(get("/api/v1/productos?categoria=CONSOLA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.productoResponseList[0].nombre").value("PS5"));
    }

    @Test
    void obtenerPorId_WhenExists_Returns200() throws Exception {
        ProductoResult r = new ProductoResult(1L, "PS5", Producto.Categoria.CONSOLA, 5, 2500.0);
        when(productoService.obtenerPorId(1L)).thenReturn(r);

        ProductoResponse response = new ProductoResponse(1L, "PS5", Producto.Categoria.CONSOLA, 5, 2500.0);
        when(productoLinkAssembler.toModel(any(ProductoResponse.class))).thenReturn(EntityModel.of(response));

        mockMvc.perform(get("/api/v1/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("PS5"));
    }

    @Test
    void obtenerPorId_WhenNotExists_Returns404() throws Exception {
        when(productoService.obtenerPorId(99L))
                .thenThrow(new ProductoNotFoundException("Producto con ID 99 no encontrado"));

        mockMvc.perform(get("/api/v1/productos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crear_WithValidData_Returns201() throws Exception {
        ProductoResult r = new ProductoResult(1L, "PS5", Producto.Categoria.CONSOLA, 5, 2500.0);
        when(productoService.crear(any(ProductoCommand.class))).thenReturn(r);

        String json = """
                {"nombre":"PS5","categoria":"CONSOLA","stock":5,"precioAlquiler":2500.0}""";

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void actualizar_WhenExists_Returns200() throws Exception {
        ProductoResult r = new ProductoResult(1L, "PS5 Pro", Producto.Categoria.CONSOLA, 3, 3500.0);
        when(productoService.actualizar(eq(1L), any(ProductoCommand.class))).thenReturn(r);

        String json = """
                {"nombre":"PS5 Pro","categoria":"CONSOLA","stock":3,"precioAlquiler":3500.0}""";

        mockMvc.perform(put("/api/v1/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("PS5 Pro"));
    }

    @Test
    void actualizarStock_WithValidData_Returns200() throws Exception {
        ProductoResult r = new ProductoResult(1L, "PS5", Producto.Categoria.CONSOLA, 8, 2500.0);
        when(productoService.actualizarStock(1L, 3)).thenReturn(r);

        String json = """
                {"cantidad":3}""";

        mockMvc.perform(patch("/api/v1/productos/1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(8));
    }

    @Test
    void eliminar_WhenExists_Returns204() throws Exception {
        doNothing().when(productoService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/productos/1"))
                .andExpect(status().isNoContent());
    }
}
