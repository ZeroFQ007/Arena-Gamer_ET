package com.example.arenawallet.service;

import com.example.arenawallet.controller.BilleteraController;
import com.example.arenawallet.dto.BilleteraResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BilleteraLinkAssembler {

    public EntityModel<BilleteraResponse> toModel(BilleteraResponse billetera) {
        EntityModel<BilleteraResponse> model = EntityModel.of(billetera);

        model.add(linkTo(methodOn(BilleteraController.class)
                .obtenerPorId(billetera.id())).withSelfRel());

        model.add(linkTo(methodOn(BilleteraController.class)
                .listar()).withRel("all"));

        model.add(linkTo(methodOn(BilleteraController.class)
                .obtenerHistorial(billetera.id())).withRel("historial"));

        model.add(linkTo(methodOn(BilleteraController.class)
                .recargarSaldo(billetera.id(), null)).withRel("recargar"));

        return model;
    }
}