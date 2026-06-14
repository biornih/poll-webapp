package oth.ics.wtp.pollsbackend.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import oth.ics.wtp.pollsbackend.dtos.AppUserDto;
import oth.ics.wtp.pollsbackend.dtos.CreateAppUserDto;
import oth.ics.wtp.pollsbackend.entities.AppUser;
import oth.ics.wtp.pollsbackend.repositories.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AppUserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public AppUserDto create(CreateAppUserDto createDto) {
        if (appUserRepository.existsByUsername(createDto.username())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already taken: " + createDto.username());
        }
        String hashedPassword = passwordEncoder.encode(createDto.password());
        AppUser user = new AppUser(createDto.username(), hashedPassword);
        AppUser saved = appUserRepository.save(user);
        return toDto(saved);
    }

    private AppUserDto toDto(AppUser user) {
        return new AppUserDto(user.getId(), user.getUsername());
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));
        return User.builder()
                .username(user.getUsername())
                .password(user.getHashedPassword())
                .roles("USER")
                .build();
    }

    public AppUserDto getByUsername(String username) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found: " + username));
        return toDto(user);
    }

}