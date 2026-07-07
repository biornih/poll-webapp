package oth.ics.wtp.pollsbackend.services;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import oth.ics.wtp.pollsbackend.dtos.AppUserDto;
import oth.ics.wtp.pollsbackend.dtos.CreateAppUserDto;
import oth.ics.wtp.pollsbackend.dtos.UpdatePasswordDto;
import oth.ics.wtp.pollsbackend.dtos.UpdateUsernameDto;
import oth.ics.wtp.pollsbackend.entities.AppUser;
import oth.ics.wtp.pollsbackend.repositories.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import oth.ics.wtp.pollsbackend.repositories.InvitationRepository;
import oth.ics.wtp.pollsbackend.repositories.PollRepository;

@Service
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    private final PollRepository pollRepository;
    private final InvitationRepository invitationRepository;


    @Autowired
    public AppUserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, PollRepository pollRepository,
                          InvitationRepository invitationRepository) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.pollRepository = pollRepository;
        this.invitationRepository = invitationRepository;
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

    public AppUser getEntityByUsername(String username) {
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found: " + username));
    }
    public java.util.Map<String, Object> getStats(String username) {
        AppUser user = getEntityByUsername(username);
        long pollsCreated = pollRepository.countByCreatorUsername(username);
        long pendingInvitations = invitationRepository
                .findByInviteeUsername(username)
                .stream()
                .filter(inv -> !inv.isHasAnswered())
                .filter(inv -> !inv.getPoll().isFinished())
                .filter(inv -> !inv.getPoll().getDueDate()
                        .isBefore(java.time.LocalDate.now()))
                .count();
        long pollsAnswered = invitationRepository
                .findByInviteeUsername(username)
                .stream()
                .filter(inv -> inv.isHasAnswered())
                .count();

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("username", user.getUsername());
        stats.put("pollsCreated", pollsCreated);
        stats.put("pendingPolls", pendingInvitations);
        stats.put("pollsAnswered", pollsAnswered);
        return stats;
    }

    @Transactional
    public AppUserDto updateUsername(String currentUsername,
                                     UpdateUsernameDto dto) {
        if (appUserRepository.existsByUsername(dto.username())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Username already taken: " + dto.username());
        }
        AppUser user = getEntityByUsername(currentUsername);
        user.setUsername(dto.username());
        AppUser saved = appUserRepository.save(user);
        return toDto(saved);
    }

    @Transactional
    public AppUserDto updatePassword(String currentUsername,
                                     UpdatePasswordDto dto) {
        AppUser user = getEntityByUsername(currentUsername);
        if (!passwordEncoder.matches(
                dto.currentPassword(), user.getHashedPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }
        if (user.getPasswordChangedAt() != null &&
                user.getPasswordChangedAt().isAfter(
                        java.time.LocalDate.now().minusDays(7))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password can only be changed once per week");
        }
        user.setHashedPassword(passwordEncoder.encode(dto.newPassword()));
        user.setPasswordChangedAt(java.time.LocalDate.now());
        appUserRepository.save(user);
        return toDto(user);
    }

}