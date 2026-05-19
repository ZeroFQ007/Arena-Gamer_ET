package com.cybergamer.loyalty_service.repository;

import com.cybergamer.loyalty_service.entity.LoyaltyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoyaltyRepository extends JpaRepository<LoyaltyAccount, Long> {
    Optional<LoyaltyAccount> findByUserId(String userId);
}