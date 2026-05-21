package oth.ics.wtp.pollsbackend.services;

import oth.ics.wtp.pollsbackend.dtos.AppUserDto;
import oth.ics.wtp.pollsbackend.dtos.CreateAppUserDto;
import oth.ics.wtp.pollsbackend.entities.AppUser;
import oth.ics.wtp.pollsbackend.repositories.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AppUserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public AppUserDto create(CreateAppUserDto createDto) {
        if (appUserRepository.existsByUsername(createDto.username())) {
            throw new RuntimeException("Username already taken: " + createDto.username());
        }
        String hashedPassword = passwordEncoder.encode(createDto.password());
        AppUser user = new AppUser(createDto.username(), hashedPassword);
        AppUser saved = appUserRepository.save(user);
        return toDto(saved);
    }

    private AppUserDto toDto(AppUser user) {
        return new AppUserDto(user.getId(), user.getUsername());
    }
}