package oth.ics.wtp.pollsbackend.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import oth.ics.wtp.pollsbackend.dtos.AppUserDto;
import oth.ics.wtp.pollsbackend.dtos.CreateAppUserDto;
import oth.ics.wtp.pollsbackend.dtos.UpdatePasswordDto;
import oth.ics.wtp.pollsbackend.dtos.UpdateUsernameDto;
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

    @SecurityRequirement(name = "basicAuth")
    @GetMapping("/users/stats")
    public java.util.Map<String, Object> getStats() {
        var auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        return appUserService.getStats(auth.getName());
    }

    @SecurityRequirement(name = "basicAuth")
    @PutMapping("/users/username")
    public AppUserDto updateUsername(@RequestBody UpdateUsernameDto dto) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return appUserService.updateUsername(auth.getName(), dto);
    }

    @SecurityRequirement(name = "basicAuth")
    @PutMapping("/users/password")
    public AppUserDto updatePassword(@RequestBody UpdatePasswordDto dto) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return appUserService.updatePassword(auth.getName(), dto);
    }
}