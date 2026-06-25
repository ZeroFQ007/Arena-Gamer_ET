package com.cybergamer.hardware_monitor.service;

import com.cybergamer.hardware_monitor.client.NotificationClient;
import com.cybergamer.hardware_monitor.dto.PcReportDTO;
import com.cybergamer.hardware_monitor.entity.PcStatus;
import com.cybergamer.hardware_monitor.repository.HardwareRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class HardwareService {

    private final HardwareRepository hardwareRepository;
    private final NotificationClient notificationClient;

    public HardwareService(HardwareRepository hardwareRepository,
                           NotificationClient notificationClient) {
        this.hardwareRepository = hardwareRepository;
        this.notificationClient = notificationClient;
    }

    public PcStatus updatePcStatus(PcReportDTO dto) {
        log.info("[HARDWARE] Recibiendo reporte de PC: {}", dto.getPcId());

        PcStatus status = hardwareRepository.findByPcId(dto.getPcId())
                .orElse(new PcStatus());

        status.setPcId(dto.getPcId());
        status.setCpuTemp(dto.getCpuTemp());
        status.setGpuTemp(dto.getGpuTemp());
        status.setStatus("ONLINE");
        status.setLastCheck(LocalDateTime.now());

        PcStatus saved = hardwareRepository.save(status);
        log.info("[HARDWARE] Estado guardado para PC {} — CPU: {}°C | GPU: {}°C",
                dto.getPcId(), dto.getCpuTemp(), dto.getGpuTemp());

        if (dto.getCpuTemp() > 85 || dto.getGpuTemp() > 90) {
            String mensaje = String.format(
                    "ALERTA: PC %s reporta temperatura critica. CPU: %.1f°C | GPU: %.1f°C",
                    dto.getPcId(), dto.getCpuTemp(), dto.getGpuTemp()
            );
            log.warn("[HARDWARE] Temperatura critica detectada en PC {} — enviando alerta", dto.getPcId());
            notificationClient.sendAlert("staff@arenagamer.cl", mensaje);
        }

        return saved;
    }

    public List<PcStatus> getAllStatuses() {
        List<PcStatus> statuses = hardwareRepository.findAll();
        log.info("[HARDWARE] Consultando estado de {} equipos", statuses.size());
        return statuses;
    }
}
