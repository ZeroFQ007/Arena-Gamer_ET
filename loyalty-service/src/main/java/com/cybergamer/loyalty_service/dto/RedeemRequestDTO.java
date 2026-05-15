package com.cybergamer.loyalty_service.dto;

public class RedeemRequestDTO {
    private String userId;
    private Long rewardId;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Long getRewardId() { return rewardId; }
    public void setRewardId(Long rewardId) { this.rewardId = rewardId; }
}