package oth.ics.wtp.pollsbackend.services;

import oth.ics.wtp.pollsbackend.dtos.*;
import oth.ics.wtp.pollsbackend.entities.*;
import oth.ics.wtp.pollsbackend.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final AnswerRepository answerRepository;
    private final PollRepository pollRepository;
    private final QuestionRepository questionRepository;
    private final AppUserService appUserService;

    @Autowired
    public InvitationService(InvitationRepository invitationRepository,
                             AnswerRepository answerRepository,
                             PollRepository pollRepository,
                             QuestionRepository questionRepository,
                             AppUserService appUserService) {
        this.invitationRepository = invitationRepository;
        this.answerRepository = answerRepository;
        this.pollRepository = pollRepository;
        this.questionRepository = questionRepository;
        this.appUserService = appUserService;
    }

    public InvitationDto invite(long pollId, InviteUserDto inviteDto,
                                String currentUsername) {
        Poll poll = findPollOrThrow(pollId);
        requireOwner(poll, currentUsername);

        if (poll.isFinished()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Poll is already finished");
        }
        if (inviteDto.username().equals(currentUsername)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "You cannot invite yourself");
        }
        if (invitationRepository.countByPollId(pollId) >= 3) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Maximum of 3 invitations per poll reached");
        }
        if (invitationRepository.existsByPollIdAndInviteeUsername(
                pollId, inviteDto.username())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User already invited: " + inviteDto.username());
        }

        AppUser invitee = appUserService.getEntityByUsername(
                inviteDto.username());
        Invitation invitation = new Invitation(poll, invitee);
        Invitation saved = invitationRepository.save(invitation);
        return toDto(saved);
    }

    public List<InvitationDto> getPendingPolls(String username) {
        return invitationRepository.findByInviteeUsername(username)
                .stream()
                .filter(inv -> !inv.isHasAnswered())
                .filter(inv -> !inv.getPoll().isFinished())
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void submitAnswers(long pollId, SubmitAnswersDto submitDto,
                              String username) {
        Poll poll = findPollOrThrow(pollId);

        if (poll.isFinished()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Poll is already finished");
        }

        Invitation invitation = invitationRepository
                .findByPollIdAndInviteeUsername(pollId, username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You are not invited to this poll"));

        if (invitation.isHasAnswered()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You have already answered this poll");
        }

        List<Question> questions = questionRepository.findByPollId(pollId);
        if (submitDto.answers().size() != questions.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You must answer all questions");
        }

        for (SubmitAnswerDto answerDto : submitDto.answers()) {
            Question question = questions.stream()
                    .filter(q -> q.getId() == answerDto.questionId())
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Question not found: " + answerDto.questionId()));

            Answer answer = new Answer(
                    question,
                    invitation,
                    answerDto.textValue(),
                    answerDto.booleanValue(),
                    answerDto.numericValue());
            answerRepository.save(answer);
        }

        invitation.setHasAnswered(true);
        invitationRepository.save(invitation);
    }

    private Poll findPollOrThrow(long pollId) {
        return pollRepository.findById(pollId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Poll not found: " + pollId));
    }

    private void requireOwner(Poll poll, String username) {
        if (!poll.getCreator().getUsername().equals(username)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Not your poll");
        }
    }

    private InvitationDto toDto(Invitation invitation) {
        PollDto pollDto = new PollDto(
                invitation.getPoll().getId(),
                invitation.getPoll().getTitle(),
                invitation.getPoll().getDescription(),
                invitation.getPoll().getDueDate(),
                invitation.getPoll().isFinished(),
                new AppUserDto(
                        invitation.getPoll().getCreator().getId(),
                        invitation.getPoll().getCreator().getUsername()),
                invitation.getPoll().getQuestions().stream()
                        .map(q -> new QuestionDto(
                                q.getId(), q.getText(), q.getType()))
                        .toList());
        return new InvitationDto(
                invitation.getId(),
                pollDto,
                new AppUserDto(
                        invitation.getInvitee().getId(),
                        invitation.getInvitee().getUsername()),
                invitation.isHasAnswered());
    }

    public List<IndividualResultDto> getIndividualResults(long pollId,
                                                          String username) {
        Poll poll = findPollOrThrow(pollId);
        requireOwner(poll, username);

        if (!poll.isFinished()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Poll is not finished yet");
        }

        java.time.LocalDate twoDaysAfterDue = poll.getDueDate().plusDays(2);
        if (java.time.LocalDate.now().isAfter(twoDaysAfterDue)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Individual results only available within 2 days of due date");
        }

        return invitationRepository.findByPollId(pollId)
                .stream()
                .filter(Invitation::isHasAnswered)
                .map(invitation -> {
                    List<AnswerResultDto> answers =
                            answerRepository.findByInvitationId(invitation.getId())
                                    .stream()
                                    .map(answer -> new AnswerResultDto(
                                            answer.getQuestion().getId(),
                                            answer.getQuestion().getText(),
                                            answer.getQuestion().getType(),
                                            answer.getTextValue(),
                                            answer.getBooleanValue(),
                                            answer.getNumericValue()))
                                    .toList();
                    return new IndividualResultDto(
                            new AppUserDto(
                                    invitation.getInvitee().getId(),
                                    invitation.getInvitee().getUsername()),
                            answers);
                })
                .toList();
    }

    public List<AggregateResultDto> getAggregateResults(long pollId,
                                                        String username) {
        Poll poll = findPollOrThrow(pollId);
        requireOwner(poll, username);

        if (!poll.isFinished()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Poll is not finished yet");
        }

        return poll.getQuestions().stream()
                .map(question -> {
                    List<oth.ics.wtp.pollsbackend.entities.Answer> answers =
                            answerRepository.findByQuestionId(question.getId());

                    return switch (question.getType()) {
                        case BOOLEAN -> {
                            long yesCount = answers.stream()
                                    .filter(a -> Boolean.TRUE.equals(
                                            a.getBooleanValue()))
                                    .count();
                            long noCount = answers.stream()
                                    .filter(a -> Boolean.FALSE.equals(
                                            a.getBooleanValue()))
                                    .count();
                            yield new AggregateResultDto(
                                    question.getId(), question.getText(),
                                    question.getType(),
                                    null, (int) yesCount, (int) noCount, null);
                        }
                        case NUMERIC -> {
                            double avg = answers.stream()
                                    .filter(a -> a.getNumericValue() != null)
                                    .mapToInt(
                                            oth.ics.wtp.pollsbackend.entities.Answer::getNumericValue)
                                    .average()
                                    .orElse(0.0);
                            yield new AggregateResultDto(
                                    question.getId(), question.getText(),
                                    question.getType(),
                                    avg, null, null, null);
                        }
                        case PLAIN_TEXT -> {
                            double avgLength = answers.stream()
                                    .filter(a -> a.getTextValue() != null)
                                    .mapToInt(a -> a.getTextValue().length())
                                    .average()
                                    .orElse(0.0);
                            yield new AggregateResultDto(
                                    question.getId(), question.getText(),
                                    question.getType(),
                                    null, null, null, avgLength);
                        }
                    };
                })
                .toList();
    }
}