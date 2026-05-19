package com.cybergamer.notificationservice.service;

import com.cybergamer.notificationservice.dto.SendNotificationDTO;
import com.cybergamer.notificationservice.entity.NotificationLog;
import com.cybergamer.notificationservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public NotificationLog sendNotification(SendNotificationDTO dto) {
        // Aquí iría la lógica real de enviar un correo o SMS usando librerías externas.
        // Por ahora, lo simulamos imprimiéndolo en la consola:
        System.out.println(">>> ENVIANDO " + dto.getChannel() + " a " + dto.getRecipient() + ": " + dto.getMessage());

        // Guardamos el registro en la base de datos
        NotificationLog log = new NotificationLog();
        log.setRecipient(dto.getRecipient());
        log.setMessage(dto.getMessage());
        log.setChannel(dto.getChannel());
        log.setStatus("SENT");
        log.setSentAt(LocalDateTime.now());

        return notificationRepository.save(log);
    }

    public List<NotificationLog> getAllLogs() {
        return notificationRepository.findAll();
    }
}