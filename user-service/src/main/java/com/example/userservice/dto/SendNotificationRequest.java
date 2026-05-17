package com.example.userservice.dto;

public class SendNotificationRequest {
    private String recipient;
    private String message;
    private String channel;

    public SendNotificationRequest() {}

    public SendNotificationRequest(String recipient, String message, String channel) {
        this.recipient = recipient;
        this.message = message;
        this.channel = channel;
    }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
}
