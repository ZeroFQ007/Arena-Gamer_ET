package com.example.arenawallet.controller;

import com.example.arenawallet.dto.BilleteraCommand;
import com.example.arenawallet.dto.BilleteraResponse;
import com.example.arenawallet.dto.BilleteraResult;
import com.example.arenawallet.exception.BilleteraNotFoundException;
import com.example.arenawallet.service.BilleteraLinkAssembler;
import com.example.arenawallet.service.BilleteraService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BilleteraController.class)
class BilleteraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BilleteraService billeteraService;

    @MockBean
    private BilleteraLinkAssembler billeteraLinkAssembler;

    @Test
    void listar_Returns200() throws Exception {
        BilleteraResult r = new BilleteraResult(1L, 10L, 5000.0, 100);
        when(billeteraService.listarTodas()).thenReturn(List.of(r));

        BilleteraResponse response = new BilleteraResponse(1L, 10L, 5000.0, 100);
        when(billeteraLinkAssembler.toModel(any(BilleteraResponse.class))).thenReturn(EntityModel.of(response));

        mockMvc.perform(get("/api/v1/billeteras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.billeteraResponseList[0].saldo").value(5000.0));
    }

    @Test
    void obtenerPorId_WhenExists_Returns200() throws Exception {
        BilleteraResult r = new BilleteraResult(1L, 10L, 5000.0, 100);
        when(billeteraService.obtenerPorId(1L)).thenReturn(r);

        BilleteraResponse response = new BilleteraResponse(1L, 10L, 5000.0, 100);
        when(billeteraLinkAssembler.toModel(any(BilleteraResponse.class))).thenReturn(EntityModel.of(response));

        mockMvc.perform(get("/api/v1/billeteras/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(5000.0));
    }

    @Test
    void obtenerPorId_WhenNotExists_Returns404() throws Exception {
        when(billeteraService.obtenerPorId(99L))
                .thenThrow(new BilleteraNotFoundException("Billetera con ID 99 no encontrada"));

        mockMvc.perform(get("/api/v1/billeteras/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crear_WithValidData_Returns201() throws Exception {
        BilleteraResult r = new BilleteraResult(1L, 10L, 10000.0, 0);
        when(billeteraService.crear(any(BilleteraCommand.class))).thenReturn(r);

        String json = """
                {"idUsuario":10,"saldo":10000.0,"puntosFidelizacion":0}""";

        mockMvc.perform(post("/api/v1/billeteras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsuario").value(10));
    }

    @Test
    void actualizar_WhenExists_Returns200() throws Exception {
        BilleteraResult r = new BilleteraResult(1L, 10L, 8000.0, 200);
        when(billeteraService.actualizar(eq(1L), any(BilleteraCommand.class))).thenReturn(r);

        String json = """
                {"idUsuario":10,"saldo":8000.0,"puntosFidelizacion":200}""";

        mockMvc.perform(put("/api/v1/billeteras/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(8000.0));
    }

    @Test
    void recargarSaldo_WithValidMonto_Returns200() throws Exception {
        BilleteraResult r = new BilleteraResult(1L, 10L, 7000.0, 100);
        when(billeteraService.recargarSaldo(1L, 2000.0)).thenReturn(r);

        String json = """
                {"monto":2000.0}""";

        mockMvc.perform(patch("/api/v1/billeteras/1/recargas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(7000.0));
    }

    @Test
    void descontarSaldo_WithValidMonto_Returns200() throws Exception {
        BilleteraResult r = new BilleteraResult(1L, 10L, 3000.0, 100);
        when(billeteraService.descontarSaldo(1L, 2000.0)).thenReturn(r);

        String json = """
                {"monto":2000.0}""";

        mockMvc.perform(patch("/api/v1/billeteras/1/descuento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(3000.0));
    }

    @Test
    void obtenerHistorial_WhenExists_Returns200() throws Exception {
        when(billeteraService.obtenerHistorial(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/billeteras/1/historial"))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar_WhenExists_Returns204() throws Exception {
        doNothing().when(billeteraService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/billeteras/1"))
                .andExpect(status().isNoContent());
    }
}
