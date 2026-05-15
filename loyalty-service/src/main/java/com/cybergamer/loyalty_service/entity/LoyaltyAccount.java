package com.cybergamer.loyalty_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "loyalty_accounts")
public class LoyaltyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    private Integer pointsBalance;

    private String tier; // BRONCE, PLATA, ORO

    // Constructor vacío (obligatorio para JPA)
    public LoyaltyAccount() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Integer getPointsBalance() { return pointsBalance; }
    public void setPointsBalance(Integer pointsBalance) { this.pointsBalance = pointsBalance; }
    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
}