package oth.ics.wtp.pollsbackend;

import oth.ics.wtp.pollsbackend.dtos.*;
import oth.ics.wtp.pollsbackend.entities.QuestionType;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PollControllerTest extends PollsBackendTestBase {

    private CreatePollDto samplePoll() {
        return new CreatePollDto(
                "My Poll",
                "A test poll",
                LocalDate.now().plusDays(7),
                List.of(
                        new CreateQuestionDto("How was it?", QuestionType.PLAIN_TEXT),
                        new CreateQuestionDto("Would you return?", QuestionType.BOOLEAN),
                        new CreateQuestionDto("Rate it", QuestionType.NUMERIC)
                )
        );
    }

    @Test
    public void testCreatePoll() {
        authenticateAsBiorni();
        PollDto poll = pollController.createPoll(samplePoll());
        assertNotNull(poll);
        assertEquals("My Poll", poll.title());
        assertEquals(3, poll.questions().size());
        assertFalse(poll.finished());
        assertEquals("biorni", poll.creator().username());
    }

    @Test
    public void testListPolls() {
        authenticateAsBiorni();
        pollController.createPoll(samplePoll());
        pollController.createPoll(new CreatePollDto(
                "Second Poll", "Another poll",
                LocalDate.now().plusDays(3),
                List.of(new CreateQuestionDto("Q1", QuestionType.BOOLEAN))));

        List<PollDto> polls = pollController.listPolls();
        assertEquals(2, polls.size());
    }

    @Test
    public void testListPollsOnlyShowsOwnPolls() {
        authenticateAsBiorni();
        pollController.createPoll(samplePoll());

        authenticateAsAlice();
        List<PollDto> alicePolls = pollController.listPolls();
        assertEquals(0, alicePolls.size());
    }

    @Test
    public void testGetPoll() {
        authenticateAsBiorni();
        PollDto created = pollController.createPoll(samplePoll());
        PollDto fetched = pollController.getPoll(created.id());
        assertEquals(created.id(), fetched.id());
        assertEquals("My Poll", fetched.title());
    }

    @Test
    public void testGetPollForbiddenForOtherUser() {
        authenticateAsBiorni();
        PollDto created = pollController.createPoll(samplePoll());

        authenticateAsAlice();
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> pollController.getPoll(created.id()));
        assertEquals(403, ex.getStatusCode().value());
    }

    @Test
    public void testUpdatePoll() {
        authenticateAsBiorni();
        PollDto created = pollController.createPoll(samplePoll());
        PollDto updated = pollController.updatePoll(created.id(),
                new UpdatePollDto("Updated Title", "New desc",
                        LocalDate.now().plusDays(10)));
        assertEquals("Updated Title", updated.title());
        assertEquals("New desc", updated.description());
    }

    @Test
    public void testDuplicatePollTitle() {
        authenticateAsBiorni();
        pollController.createPoll(samplePoll());
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> pollController.createPoll(samplePoll()));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    public void testFinishPoll() {
        authenticateAsBiorni();
        PollDto created = pollController.createPoll(samplePoll());
        PollDto finished = pollController.finishPoll(created.id());
        assertTrue(finished.finished());
    }

    @Test
    public void testFinishPollAlreadyFinished() {
        authenticateAsBiorni();
        PollDto created = pollController.createPoll(samplePoll());
        pollController.finishPoll(created.id());
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> pollController.finishPoll(created.id()));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    public void testDeletePoll() {
        authenticateAsBiorni();
        PollDto created = pollController.createPoll(samplePoll());
        pollController.deletePoll(created.id());
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> pollController.getPoll(created.id()));
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    public void testDeletePollForbiddenForOtherUser() {
        authenticateAsBiorni();
        PollDto created = pollController.createPoll(samplePoll());

        authenticateAsAlice();
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> pollController.deletePoll(created.id()));
        assertEquals(403, ex.getStatusCode().value());
    }
}