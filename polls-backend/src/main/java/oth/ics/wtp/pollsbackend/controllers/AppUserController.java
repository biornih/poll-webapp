package oth.ics.wtp.pollsbackend.controllers;

import oth.ics.wtp.pollsbackend.dtos.AppUserDto;
import oth.ics.wtp.pollsbackend.dtos.CreateAppUserDto;
import oth.ics.wtp.pollsbackend.services.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
public class AppUserController {

    private final AppUserService appUserService;

    @Autowired
    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users")
    public AppUserDto createUser(@RequestBody CreateAppUserDto createDto) {
        return appUserService.create(createDto);
    }

    @SecurityRequirement(name = "basicAuth")
    @GetMapping("/users/current")
    public AppUserDto getCurrentUser() {
        var auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        return appUserService.getByUsername(auth.getName());
    }
}