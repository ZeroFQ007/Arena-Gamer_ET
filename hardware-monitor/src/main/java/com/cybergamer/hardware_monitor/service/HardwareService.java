package com.cybergamer.hardware_monitor.service;

import com.cybergamer.hardware_monitor.dto.PcReportDTO;
import com.cybergamer.hardware_monitor.entity.PcStatus;
import com.cybergamer.hardware_monitor.repository.HardwareRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HardwareService {

    private final HardwareRepository hardwareRepository;

    public HardwareService(HardwareRepository hardwareRepository) {
        this.hardwareRepository = hardwareRepository;
    }

    public PcStatus updatePcStatus(PcReportDTO dto) {
        PcStatus status = hardwareRepository.findByPcId(dto.getPcId())
                .orElse(new PcStatus());

        status.setPcId(dto.getPcId());
        status.setCpuTemp(dto.getCpuTemp());
        status.setGpuTemp(dto.getGpuTemp());
        status.setStatus("ONLINE");
        status.setLastCheck(LocalDateTime.now());

        return hardwareRepository.save(status);
    }

    public List<PcStatus> getAllStatuses() {
        return hardwareRepository.findAll();
    }
}