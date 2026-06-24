package com.cybergamer.hardware_monitor.service;

import com.cybergamer.hardware_monitor.controller.HardwareController;
import com.cybergamer.hardware_monitor.entity.PcStatus;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class HardwareLinkAssembler {

    public EntityModel<PcStatus> toModel(PcStatus status) {
        EntityModel<PcStatus> model = EntityModel.of(status);

        // Link self → lista de todos los estados
        model.add(linkTo(methodOn(HardwareController.class)
                .getStatuses()).withSelfRel());

        // Link para reportar un nuevo estado de este mismo PC
        model.add(Link.of("/api/v1/hardware/report", "report"));

        // Si la temperatura es crítica, agregar link de alerta
        if (status.getCpuTemp() != null && status.getGpuTemp() != null) {
            if (status.getCpuTemp() > 85 || status.getGpuTemp() > 90) {
                model.add(Link.of("/api/v1/notifications/send", "alert"));
            }
        }

        return model;
    }
}
