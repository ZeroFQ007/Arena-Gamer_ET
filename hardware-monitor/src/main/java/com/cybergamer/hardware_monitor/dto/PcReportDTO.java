package com.cybergamer.hardware_monitor.dto;

public class PcReportDTO {
    private String pcId;
    private Double cpuTemp;
    private Double gpuTemp;

    // Getters y Setters
    public String getPcId() { return pcId; }
    public void setPcId(String pcId) { this.pcId = pcId; }
    public Double getCpuTemp() { return cpuTemp; }
    public void setCpuTemp(Double cpuTemp) { this.cpuTemp = cpuTemp; }
    public Double getGpuTemp() { return gpuTemp; }
    public void setGpuTemp(Double gpuTemp) { this.gpuTemp = gpuTemp; }
}