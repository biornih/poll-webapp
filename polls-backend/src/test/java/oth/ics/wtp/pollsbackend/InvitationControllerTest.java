package oth.ics.wtp.pollsbackend;

import oth.ics.wtp.pollsbackend.dtos.*;
import oth.ics.wtp.pollsbackend.entities.QuestionType;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InvitationControllerTest extends PollsBackendTestBase {

    private PollDto createPollAsBiorni() {
        authenticateAsBiorni();
        return pollController.createPoll(new CreatePollDto(
                "Test Poll", "desc",
                LocalDate.now().plusDays(7),
                List.of(
                        new CreateQuestionDto("Text Q", QuestionType.PLAIN_TEXT),
                        new CreateQuestionDto("Bool Q", QuestionType.BOOLEAN),
                        new CreateQuestionDto("Num Q", QuestionType.NUMERIC)
                )
        ));
    }

    @Test
    public void testInviteUser() {
        PollDto poll = createPollAsBiorni();
        authenticateAsBiorni();
        InvitationDto invitation = invitationController.invite(
                poll.id(), new InviteUserDto("alice"));
        assertNotNull(invitation);
        assertEquals("alice", invitation.invitee().username());
        assertFalse(invitation.hasAnswered());
    }

    @Test
    public void testCannotInviteSelf() {
        PollDto poll = createPollAsBiorni();
        authenticateAsBiorni();
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> invitationController.invite(
                        poll.id(), new InviteUserDto("biorni")));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    public void testCannotInviteMoreThanThree() {
        PollDto poll = createPollAsBiorni();

        appUserController.createUser(
                new CreateAppUserDto("user2", "pass"));
        appUserController.createUser(
                new CreateAppUserDto("user3", "pass"));
        appUserController.createUser(
                new CreateAppUserDto("user4", "pass"));

        authenticateAsBiorni();
        invitationController.invite(poll.id(), new InviteUserDto("alice"));
        invitationController.invite(poll.id(), new InviteUserDto("user2"));
        invitationController.invite(poll.id(), new InviteUserDto("user3"));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> invitationController.invite(
                        poll.id(), new InviteUserDto("user4")));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    public void testPendingPolls() {
        PollDto poll = createPollAsBiorni();
        authenticateAsBiorni();
        invitationController.invite(poll.id(), new InviteUserDto("alice"));

        authenticateAsAlice();
        List<InvitationDto> pending = invitationController.getPendingPolls();
        assertEquals(1, pending.size());
        assertEquals(poll.id(), pending.get(0).poll().id());
    }

    @Test
    public void testSubmitAnswers() {
        PollDto poll = createPollAsBiorni();
        authenticateAsBiorni();
        invitationController.invite(poll.id(), new InviteUserDto("alice"));

        authenticateAsAlice();
        List<InvitationDto> pending = invitationController.getPendingPolls();
        PollDto pendingPoll = pending.get(0).poll();

        SubmitAnswersDto submitDto = new SubmitAnswersDto(List.of(
                new SubmitAnswerDto(
                        pendingPoll.questions().get(0).id(),
                        "Great!", null, null),
                new SubmitAnswerDto(
                        pendingPoll.questions().get(1).id(),
                        null, true, null),
                new SubmitAnswerDto(
                        pendingPoll.questions().get(2).id(),
                        null, null, 4)
        ));
        invitationController.submitAnswers(poll.id(), submitDto);

        List<InvitationDto> pendingAfter =
                invitationController.getPendingPolls();
        assertEquals(0, pendingAfter.size());
    }

    @Test
    public void testCannotSubmitAnswersTwice() {
        PollDto poll = createPollAsBiorni();
        authenticateAsBiorni();
        invitationController.invite(poll.id(), new InviteUserDto("alice"));

        authenticateAsAlice();
        List<InvitationDto> pending = invitationController.getPendingPolls();
        PollDto pendingPoll = pending.get(0).poll();

        SubmitAnswersDto submitDto = new SubmitAnswersDto(List.of(
                new SubmitAnswerDto(
                        pendingPoll.questions().get(0).id(),
                        "Great!", null, null),
                new SubmitAnswerDto(
                        pendingPoll.questions().get(1).id(),
                        null, true, null),
                new SubmitAnswerDto(
                        pendingPoll.questions().get(2).id(),
                        null, null, 4)
        ));
        invitationController.submitAnswers(poll.id(), submitDto);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> invitationController.submitAnswers(poll.id(), submitDto));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    public void testAggregateResults() {
        PollDto poll = createPollAsBiorni();
        authenticateAsBiorni();
        invitationController.invite(poll.id(), new InviteUserDto("alice"));

        authenticateAsAlice();
        List<InvitationDto> pending = invitationController.getPendingPolls();
        PollDto pendingPoll = pending.get(0).poll();

        invitationController.submitAnswers(poll.id(),
                new SubmitAnswersDto(List.of(
                        new SubmitAnswerDto(
                                pendingPoll.questions().get(0).id(),
                                "Great experience", null, null),
                        new SubmitAnswerDto(
                                pendingPoll.questions().get(1).id(),
                                null, true, null),
                        new SubmitAnswerDto(
                                pendingPoll.questions().get(2).id(),
                                null, null, 4)
                )));

        authenticateAsBiorni();
        pollController.finishPoll(poll.id());

        List<AggregateResultDto> results =
                invitationController.getAggregateResults(poll.id());

        AggregateResultDto boolResult = results.stream()
                .filter(r -> r.type() == QuestionType.BOOLEAN)
                .findFirst().orElseThrow();
        assertEquals(1, boolResult.yesCount());
        assertEquals(0, boolResult.noCount());

        AggregateResultDto numResult = results.stream()
                .filter(r -> r.type() == QuestionType.NUMERIC)
                .findFirst().orElseThrow();
        assertEquals(4.0, numResult.average());

        AggregateResultDto textResult = results.stream()
                .filter(r -> r.type() == QuestionType.PLAIN_TEXT)
                .findFirst().orElseThrow();
        assertEquals(16.0, textResult.averageLength());
    }
}