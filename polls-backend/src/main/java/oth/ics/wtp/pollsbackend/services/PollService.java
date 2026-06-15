package oth.ics.wtp.pollsbackend.services;

import oth.ics.wtp.pollsbackend.dtos.*;
import oth.ics.wtp.pollsbackend.entities.*;
import oth.ics.wtp.pollsbackend.repositories.PollRepository;
import oth.ics.wtp.pollsbackend.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PollService {

    private final PollRepository pollRepository;
    private final QuestionRepository questionRepository;
    private final AppUserService appUserService;

    @Autowired
    public PollService(PollRepository pollRepository,
                       QuestionRepository questionRepository,
                       AppUserService appUserService) {
        this.pollRepository = pollRepository;
        this.questionRepository = questionRepository;
        this.appUserService = appUserService;
    }

    public PollDto create(CreatePollDto createDto, String username) {
        if (pollRepository.existsByTitle(createDto.title())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Poll title already taken: " + createDto.title());
        }
        AppUser creator = appUserService.getEntityByUsername(username);
        Poll poll = new Poll(
                createDto.title(),
                createDto.description(),
                createDto.dueDate(),
                creator);
        Poll saved = pollRepository.save(poll);
        for (CreateQuestionDto q : createDto.questions()) {
            Question question = new Question(q.text(), q.type(), saved);
            questionRepository.save(question);
        }
        return toDto(pollRepository.findById(saved.getId()).orElseThrow());
    }

    public List<PollDto> listByCreator(String username) {
        return pollRepository.findByCreatorUsername(username)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public PollDto get(long id, String username) {
        Poll poll = findPollOrThrow(id);
        if (!poll.getCreator().getUsername().equals(username)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Not your poll");
        }
        return toDto(poll);
    }

    @Transactional
    public PollDto update(long id, UpdatePollDto updateDto, String username) {
        Poll poll = findPollOrThrow(id);
        requireOwner(poll, username);
        if (!poll.getTitle().equals(updateDto.title()) &&
                pollRepository.existsByTitle(updateDto.title())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Poll title already taken: " + updateDto.title());
        }
        poll.setTitle(updateDto.title());
        poll.setDescription(updateDto.description());
        poll.setDueDate(updateDto.dueDate());
        return toDto(pollRepository.save(poll));
    }

    @Transactional
    public PollDto finish(long id, String username) {
        Poll poll = findPollOrThrow(id);
        requireOwner(poll, username);
        if (poll.isFinished()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Poll is already finished");
        }
        poll.setFinished(true);
        return toDto(pollRepository.save(poll));
    }

    @Transactional
    public void delete(long id, String username) {
        Poll poll = findPollOrThrow(id);
        requireOwner(poll, username);
        questionRepository.deleteByPollId(id);
        pollRepository.deleteById(id);
    }

    private Poll findPollOrThrow(long id) {
        return pollRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Poll not found: " + id));
    }

    private void requireOwner(Poll poll, String username) {
        if (!poll.getCreator().getUsername().equals(username)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Not your poll");
        }
    }

    private QuestionDto toQuestionDto(Question q) {
        return new QuestionDto(q.getId(), q.getText(), q.getType());
    }

    private PollDto toDto(Poll poll) {
        List<QuestionDto> questions = poll.getQuestions()
                .stream()
                .map(this::toQuestionDto)
                .toList();
        AppUserDto creator = new AppUserDto(
                poll.getCreator().getId(),
                poll.getCreator().getUsername());
        return new PollDto(
                poll.getId(),
                poll.getTitle(),
                poll.getDescription(),
                poll.getDueDate(),
                poll.isFinished(),
                creator,
                questions);
    }
}