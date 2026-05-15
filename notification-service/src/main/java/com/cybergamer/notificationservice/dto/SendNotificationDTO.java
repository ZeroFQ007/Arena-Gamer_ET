package com.cybergamer.notificationservice.dto;

public class SendNotificationDTO {
    private String recipient;
    private String message;
    private String channel;

    // Getters y Setters
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
}