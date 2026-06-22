package oth.ics.wtp.pollsbackend.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import oth.ics.wtp.pollsbackend.dtos.*;
import oth.ics.wtp.pollsbackend.services.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "basicAuth")
@RestController
public class InvitationController {

    private final InvitationService invitationService;

    @Autowired
    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/polls/{pollId}/invite")
    public InvitationDto invite(@PathVariable long pollId,
                                @RequestBody InviteUserDto inviteDto) {
        return invitationService.invite(pollId, inviteDto, currentUsername());
    }

    @GetMapping("/polls/pending")
    public List<InvitationDto> getPendingPolls() {
        return invitationService.getPendingPolls(currentUsername());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/polls/{pollId}/participate")
    public void submitAnswers(@PathVariable long pollId,
                              @RequestBody SubmitAnswersDto submitDto) {
        invitationService.submitAnswers(pollId, submitDto, currentUsername());
    }

    private String currentUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication().getName();
    }
    @GetMapping("/polls/{pollId}/results/individual")
    public List<IndividualResultDto> getIndividualResults(
            @PathVariable long pollId) {
        return invitationService.getIndividualResults(
                pollId, currentUsername());
    }

    @GetMapping("/polls/{pollId}/results/aggregate")
    public List<AggregateResultDto> getAggregateResults(
            @PathVariable long pollId) {
        return invitationService.getAggregateResults(
                pollId, currentUsername());
    }
}