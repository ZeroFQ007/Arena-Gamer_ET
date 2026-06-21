package com.cybergamer.hardware_monitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos del reporte de temperatura de un equipo")
public class PcReportDTO {

    @Schema(description = "Identificador del PC", example = "PC-01")
    private String pcId;

    @Schema(description = "Temperatura de la CPU en grados Celsius", example = "65.0")
    private Double cpuTemp;

    @Schema(description = "Temperatura de la GPU en grados Celsius", example = "70.0")
    private Double gpuTemp;

    public String getPcId() { return pcId; }
    public void setPcId(String pcId) { this.pcId = pcId; }
    public Double getCpuTemp() { return cpuTemp; }
    public void setCpuTemp(Double cpuTemp) { this.cpuTemp = cpuTemp; }
    public Double getGpuTemp() { return gpuTemp; }
    public void setGpuTemp(Double gpuTemp) { this.gpuTemp = gpuTemp; }
}