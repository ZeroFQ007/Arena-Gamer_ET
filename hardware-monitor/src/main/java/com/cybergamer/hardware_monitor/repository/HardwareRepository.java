package com.cybergamer.hardware_monitor.repository;

import com.cybergamer.hardware_monitor.entity.PcStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface HardwareRepository extends JpaRepository<PcStatus, Long> {
    Optional<PcStatus> findByPcId(String pcId);
}