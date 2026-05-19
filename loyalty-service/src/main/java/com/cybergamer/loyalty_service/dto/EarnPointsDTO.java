package com.cybergamer.loyalty_service.dto;

public class EarnPointsDTO {
    private String userId;
    private Integer amount;
    private String reason;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}