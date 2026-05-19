package com.cybergamer.notificationservice.repository;

import com.cybergamer.notificationservice.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationLog, Long> {
}