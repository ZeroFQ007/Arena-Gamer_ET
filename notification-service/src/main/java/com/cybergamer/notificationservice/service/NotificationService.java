package com.cybergamer.notificationservice.service;

import com.cybergamer.notificationservice.dto.SendNotificationDTO;
import com.cybergamer.notificationservice.entity.NotificationLog;
import com.cybergamer.notificationservice.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public NotificationLog sendNotification(SendNotificationDTO dto) {
        log.info("Enviando notificacion: canal={}, destinatario={}, mensaje={}",
                dto.getChannel(), dto.getRecipient(), dto.getMessage());

        NotificationLog notificationLog = new NotificationLog();
        notificationLog.setRecipient(dto.getRecipient());
        notificationLog.setMessage(dto.getMessage());
        notificationLog.setChannel(dto.getChannel());
        notificationLog.setStatus("SENT");
        notificationLog.setSentAt(LocalDateTime.now());

        NotificationLog saved = notificationRepository.save(notificationLog);
        log.info("Notificacion registrada: id={}, canal={}, destinatario={}",
                saved.getId(), saved.getChannel(), saved.getRecipient());
        return saved;
    }

    public List<NotificationLog> getAllLogs() {
        List<NotificationLog> logs = notificationRepository.findAll();
        log.info("Consultando historial de notificaciones: {} registros", logs.size());
        return logs;
    }
}