package com.cybergamer.hardware_monitor.controller;

import com.cybergamer.hardware_monitor.dto.PcReportDTO;
import com.cybergamer.hardware_monitor.entity.PcStatus;
import com.cybergamer.hardware_monitor.service.HardwareService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hardware")
public class HardwareController {

    private final HardwareService hardwareService;

    public HardwareController(HardwareService hardwareService) {
        this.hardwareService = hardwareService;
    }

    @PostMapping("/report")
    public ResponseEntity<PcStatus> reportStatus(@RequestBody PcReportDTO request) {
        return ResponseEntity.ok(hardwareService.updatePcStatus(request));
    }

    @GetMapping("/status")
    public ResponseEntity<List<PcStatus>> getStatuses() {
        return ResponseEntity.ok(hardwareService.getAllStatuses());
    }
}