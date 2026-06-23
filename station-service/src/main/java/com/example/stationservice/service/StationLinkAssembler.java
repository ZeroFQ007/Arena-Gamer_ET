package com.example.stationservice.service;

import com.example.stationservice.controller.StationController;
import com.example.stationservice.model.Station;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class StationLinkAssembler {

    public EntityModel<Station> toModel(Station station) {
        EntityModel<Station> model = EntityModel.of(station);

        model.add(linkTo(methodOn(StationController.class)
                .getById(station.getId())).withSelfRel());

        model.add(linkTo(methodOn(StationController.class)
                .getAll()).withRel("all"));

        if (station.isAvailable()) {
            model.add(linkTo(methodOn(StationController.class)
                    .update(station.getId(), null)).withRel("update"));
        }

        return model;
    }
}