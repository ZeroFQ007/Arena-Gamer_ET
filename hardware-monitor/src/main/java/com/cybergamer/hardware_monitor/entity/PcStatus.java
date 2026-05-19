package com.cybergamer.hardware_monitor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pc_status")
public class PcStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pcId;        // Ej: "VIP-01", "Gral-15"
    private Double cpuTemp;     // Temperatura del procesador
    private Double gpuTemp;     // Temperatura de la tarjeta de video
    private String status;      // ONLINE, OFFLINE, MAINTENANCE
    private LocalDateTime lastCheck;

    public PcStatus() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPcId() { return pcId; }
    public void setPcId(String pcId) { this.pcId = pcId; }
    public Double getCpuTemp() { return cpuTemp; }
    public void setCpuTemp(Double cpuTemp) { this.cpuTemp = cpuTemp; }
    public Double getGpuTemp() { return gpuTemp; }
    public void setGpuTemp(Double gpuTemp) { this.gpuTemp = gpuTemp; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getLastCheck() { return lastCheck; }
    public void setLastCheck(LocalDateTime lastCheck) { this.lastCheck = lastCheck; }
}