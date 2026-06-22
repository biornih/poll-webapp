package oth.ics.wtp.pollsbackend;

import oth.ics.wtp.pollsbackend.dtos.AppUserDto;
import oth.ics.wtp.pollsbackend.dtos.CreateAppUserDto;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

public class AppUserControllerTest extends PollsBackendTestBase {

    @Test
    public void testRegisterUser() {
        assertNotNull(userBiorni);
        assertEquals("biorni", userBiorni.username());
        assertTrue(userBiorni.id() > 0);
    }

    @Test
    public void testRegisterDuplicateUsername() {
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> appUserController.createUser(
                        new CreateAppUserDto("biorni", "other")));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    public void testGetCurrentUser() {
        authenticateAsBiorni();
        AppUserDto current = appUserController.getCurrentUser();
        assertEquals("biorni", current.username());
    }
}