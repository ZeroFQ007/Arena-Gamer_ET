package com.cybergamer.loyalty_service.service;

import com.cybergamer.loyalty_service.controller.LoyaltyController;
import com.cybergamer.loyalty_service.entity.LoyaltyAccount;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class LoyaltyLinkAssembler {

    public EntityModel<LoyaltyAccount> toModel(LoyaltyAccount account) {
        EntityModel<LoyaltyAccount> model = EntityModel.of(account);

        model.add(linkTo(methodOn(LoyaltyController.class)
                .getLoyaltyProfile(account.getUserId())).withSelfRel());

        if (account.getPointsBalance() >= 500) {
            model.add(Link.of("/api/v1/loyalty/redeem", "redeem"));
        }

        return model;
    }
}