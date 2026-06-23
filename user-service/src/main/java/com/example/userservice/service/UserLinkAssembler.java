package com.example.userservice.service;

import com.example.userservice.controller.UserController;
import com.example.userservice.model.User;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserLinkAssembler {

    public EntityModel<User> toModel(User user) {
        EntityModel<User> model = EntityModel.of(user);

        model.add(linkTo(methodOn(UserController.class)
                .getById(user.getId())).withSelfRel());

        model.add(linkTo(methodOn(UserController.class)
                .getAll()).withRel("all"));

        if (user.isActive()) {
            model.add(linkTo(methodOn(UserController.class)
                    .update(user.getId(), null)).withRel("update"));
        }

        return model;
    }
}