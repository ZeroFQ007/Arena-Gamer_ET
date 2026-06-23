package com.example.sessionservice.service;

import com.example.sessionservice.controller.SessionController;
import com.example.sessionservice.dto.SessionResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SessionLinkAssembler {

    public EntityModel<SessionResponse> toModel(SessionResponse session) {
        EntityModel<SessionResponse> model = EntityModel.of(session);

        model.add(linkTo(methodOn(SessionController.class)
                .getById(session.id())).withSelfRel());

        model.add(linkTo(methodOn(SessionController.class)
                .getAll()).withRel("all"));

        if (session.status() == com.example.sessionservice.model.Session.SessionStatus.ACTIVE) {
            model.add(linkTo(methodOn(SessionController.class)
                    .finishSession(session.id())).withRel("finish"));
            model.add(linkTo(methodOn(SessionController.class)
                    .cancelSession(session.id())).withRel("cancel"));
        }

        return model;
    }
}