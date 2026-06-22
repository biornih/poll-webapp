package oth.ics.wtp.pollsbackend;

import oth.ics.wtp.pollsbackend.controllers.AppUserController;
import oth.ics.wtp.pollsbackend.controllers.InvitationController;
import oth.ics.wtp.pollsbackend.controllers.PollController;
import oth.ics.wtp.pollsbackend.dtos.CreateAppUserDto;
import oth.ics.wtp.pollsbackend.dtos.AppUserDto;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public abstract class PollsBackendTestBase {

    @Autowired
    protected AppUserController appUserController;

    @Autowired
    protected PollController pollController;

    @Autowired
    protected InvitationController invitationController;

    protected AppUserDto userBiorni;
    protected AppUserDto userAlice;

    @BeforeEach
    public void beforeEach() {
        userBiorni = appUserController.createUser(
                new CreateAppUserDto("biorni", "secret123"));
        userAlice = appUserController.createUser(
                new CreateAppUserDto("alice", "pass456"));
    }

    protected void authenticateAs(String username) {
        UserDetails user = User.builder()
                .username(username)
                .password("ignored")
                .roles("USER")
                .build();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    protected void authenticateAsBiorni() {
        authenticateAs("biorni");
    }

    protected void authenticateAsAlice() {
        authenticateAs("alice");
    }
}