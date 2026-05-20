package com.cybergamer.hardware_monitor.service;

import com.cybergamer.hardware_monitor.client.NotificationClient;
import com.cybergamer.hardware_monitor.dto.PcReportDTO;
import com.cybergamer.hardware_monitor.entity.PcStatus;
import com.cybergamer.hardware_monitor.repository.HardwareRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Service
public class HardwareService {

    private static final Logger log = Logger.getLogger(HardwareService.class.getName());
    private final HardwareRepository hardwareRepository;
    private final NotificationClient notificationClient;

    public HardwareService(HardwareRepository hardwareRepository,
                           NotificationClient notificationClient) {
        this.hardwareRepository = hardwareRepository;
        this.notificationClient = notificationClient;
    }

    public PcStatus updatePcStatus(PcReportDTO dto) {
        PcStatus status = hardwareRepository.findByPcId(dto.getPcId())
                .orElse(new PcStatus());

        status.setPcId(dto.getPcId());
        status.setCpuTemp(dto.getCpuTemp());
        status.setGpuTemp(dto.getGpuTemp());
        status.setStatus("ONLINE");
        status.setLastCheck(LocalDateTime.now());

        PcStatus saved = hardwareRepository.save(status);

        if (dto.getCpuTemp() > 85 || dto.getGpuTemp() > 90) {
            String mensaje = String.format(
                    "ALERTA: PC %s reporta temperatura critica. CPU: %.1f°C | GPU: %.1f°C",
                    dto.getPcId(), dto.getCpuTemp(), dto.getGpuTemp()
            );
            log.warning("Temperatura critica en " + dto.getPcId());
            notificationClient.sendAlert("staff@arenagamer.cl", mensaje);
        }

        return saved;
    }

    public List<PcStatus> getAllStatuses() {
        return hardwareRepository.findAll();
    }
}