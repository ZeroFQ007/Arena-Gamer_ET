package com.cybergamer.hardware_monitor.controller;

import com.cybergamer.hardware_monitor.dto.PcReportDTO;
import com.cybergamer.hardware_monitor.entity.PcStatus;
import com.cybergamer.hardware_monitor.service.HardwareLinkAssembler;
import com.cybergamer.hardware_monitor.service.HardwareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Hardware", description = "Operaciones para monitorear temperatura de equipos en Arena Gamer")
@RestController
@RequestMapping("/api/v1/hardware")
public class HardwareController {

    private final HardwareService hardwareService;
    private final HardwareLinkAssembler linkAssembler;

    public HardwareController(HardwareService hardwareService,
                               HardwareLinkAssembler linkAssembler) {
        this.hardwareService = hardwareService;
        this.linkAssembler = linkAssembler;
    }

    @Operation(summary = "Reportar estado de hardware",
               description = "Registra la temperatura de CPU y GPU de un PC. Si CPU>85° o GPU>90° envía alerta a notification-service")
    @ApiResponse(responseCode = "200", description = "Estado registrado correctamente")
    @PostMapping("/report")
    public ResponseEntity<EntityModel<PcStatus>> reportStatus(@RequestBody PcReportDTO request) {
        PcStatus saved = hardwareService.updatePcStatus(request);
        return ResponseEntity.ok(linkAssembler.toModel(saved));
    }

    @Operation(summary = "Ver estado de equipos",
               description = "Obtiene el último estado reportado de todos los equipos con enlaces HATEOAS")
    @ApiResponse(responseCode = "200", description = "Estados obtenidos correctamente")
    @GetMapping("/status")
    public ResponseEntity<CollectionModel<EntityModel<PcStatus>>> getStatuses() {
        List<EntityModel<PcStatus>> modelos = hardwareService.getAllStatuses()
                .stream()
                .map(linkAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<PcStatus>> collection = CollectionModel.of(
                modelos,
                linkTo(methodOn(HardwareController.class).getStatuses()).withSelfRel()
        );

        return ResponseEntity.ok(collection);
    }
}
