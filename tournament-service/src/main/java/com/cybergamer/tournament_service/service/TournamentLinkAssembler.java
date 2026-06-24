package com.cybergamer.tournament_service.service;

import com.cybergamer.tournament_service.controller.TournamentController;
import com.cybergamer.tournament_service.entity.Tournament;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TournamentLinkAssembler {

    public EntityModel<Tournament> toModel(Tournament tournament) {
        EntityModel<Tournament> model = EntityModel.of(tournament);

        // Self link → GET por ID
        model.add(linkTo(methodOn(TournamentController.class)
                .getTournamentById(tournament.getId())).withSelfRel());

        // Link a la lista completa
        model.add(linkTo(methodOn(TournamentController.class)
                .getTournaments()).withRel("all"));

        // Si el torneo está OPEN, tiene capacidad disponible
        if ("OPEN".equals(tournament.getStatus())
                && tournament.getCurrentTeams() < tournament.getMaxTeams()) {
            model.add(Link.of("/api/v1/tournaments", "register"));
        }

        return model;
    }
}
