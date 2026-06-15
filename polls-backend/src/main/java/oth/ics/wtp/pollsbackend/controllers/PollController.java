package oth.ics.wtp.pollsbackend.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import oth.ics.wtp.pollsbackend.dtos.CreatePollDto;
import oth.ics.wtp.pollsbackend.dtos.PollDto;
import oth.ics.wtp.pollsbackend.dtos.UpdatePollDto;
import oth.ics.wtp.pollsbackend.services.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "basicAuth")
@RestController
public class PollController {

    private final PollService pollService;

    @Autowired
    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/polls")
    public PollDto createPoll(@RequestBody CreatePollDto createDto) {
        return pollService.create(createDto, currentUsername());
    }

    @GetMapping("/polls")
    public List<PollDto> listPolls() {
        return pollService.listByCreator(currentUsername());
    }

    @GetMapping("/polls/{id}")
    public PollDto getPoll(@PathVariable long id) {
        return pollService.get(id, currentUsername());
    }

    @PutMapping("/polls/{id}")
    public PollDto updatePoll(@PathVariable long id,
                              @RequestBody UpdatePollDto updateDto) {
        return pollService.update(id, updateDto, currentUsername());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/polls/{id}")
    public void deletePoll(@PathVariable long id) {
        pollService.delete(id, currentUsername());
    }

    @PostMapping("/polls/{id}/finish")
    public PollDto finishPoll(@PathVariable long id) {
        return pollService.finish(id, currentUsername());
    }

    private String currentUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication().getName();
    }
}