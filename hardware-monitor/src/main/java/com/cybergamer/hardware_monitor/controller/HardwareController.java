package com.cybergamer.hardware_monitor.controller;

import com.cybergamer.hardware_monitor.dto.PcReportDTO;
import com.cybergamer.hardware_monitor.entity.PcStatus;
import com.cybergamer.hardware_monitor.service.HardwareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Hardware", description = "Operaciones para monitorear temperatura de equipos en Arena Gamer")
@RestController
@RequestMapping("/api/v1/hardware")
public class HardwareController {

    private final HardwareService hardwareService;

    public HardwareController(HardwareService hardwareService) {
        this.hardwareService = hardwareService;
    }

    @Operation(summary = "Reportar estado de hardware", description = "Registra la temperatura de CPU y GPU de un PC. Si CPU>85° o GPU>90° envía alerta a notification-service")
    @ApiResponse(responseCode = "200", description = "Estado registrado correctamente")
    @PostMapping("/report")
    public ResponseEntity<PcStatus> reportStatus(@RequestBody PcReportDTO request) {
        return ResponseEntity.ok(hardwareService.updatePcStatus(request));
    }

    @Operation(summary = "Ver estado de equipos", description = "Obtiene el último estado reportado de todos los equipos")
    @ApiResponse(responseCode = "200", description = "Estados obtenidos correctamente")
    @GetMapping("/status")
    public ResponseEntity<List<PcStatus>> getStatuses() {
        return ResponseEntity.ok(hardwareService.getAllStatuses());
    }
}