package com.example.arenainventory.service;

import com.example.arenainventory.controller.ProductoController;
import com.example.arenainventory.dto.ProductoResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductoLinkAssembler {

    public EntityModel<ProductoResponse> toModel(ProductoResponse producto) {
        EntityModel<ProductoResponse> model = EntityModel.of(producto);

        model.add(linkTo(methodOn(ProductoController.class)
                .obtenerPorId(producto.id())).withSelfRel());

        model.add(linkTo(methodOn(ProductoController.class)
                .listar(null)).withRel("all"));

        if (producto.stock() > 0) {
            model.add(linkTo(methodOn(ProductoController.class)
                    .actualizarStock(producto.id(), null)).withRel("update-stock"));
        }

        return model;
    }
}