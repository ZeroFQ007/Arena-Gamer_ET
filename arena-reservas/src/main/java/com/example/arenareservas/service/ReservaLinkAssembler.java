package com.example.arenareservas.service;

import com.example.arenareservas.controller.ReservaController;
import com.example.arenareservas.dto.ReservaResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ReservaLinkAssembler {

    public EntityModel<ReservaResponse> toModel(ReservaResponse reserva) {
        EntityModel<ReservaResponse> model = EntityModel.of(reserva);

        model.add(linkTo(methodOn(ReservaController.class)
                .obtenerPorId(reserva.id())).withSelfRel());

        model.add(linkTo(methodOn(ReservaController.class)
                .listar(null)).withRel("all"));

        model.add(linkTo(methodOn(ReservaController.class)
                .obtenerHistorial(reserva.id())).withRel("history"));

        if ("NUEVA".equalsIgnoreCase(reserva.estado())) {
            model.add(linkTo(methodOn(ReservaController.class)
                    .cambiarEstado(reserva.id(), null)).withRel("change-status"));
        }

        return model;
    }
}