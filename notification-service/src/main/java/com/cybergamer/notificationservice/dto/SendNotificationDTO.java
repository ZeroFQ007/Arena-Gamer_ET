package com.cybergamer.notificationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos para enviar una notificación")
public class SendNotificationDTO {

    @Schema(description = "Destinatario de la notificación", example = "jugador@arenagamer.cl")
    private String recipient;

    @Schema(description = "Contenido del mensaje", example = "Tu sesión ha finalizado")
    private String message;

    @Schema(description = "Canal de envío", example = "EMAIL")
    private String channel;

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
}