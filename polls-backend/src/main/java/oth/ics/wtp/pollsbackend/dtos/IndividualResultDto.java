package oth.ics.wtp.pollsbackend.dtos;

import java.util.List;

public record IndividualResultDto(
        AppUserDto participant,
        List<AnswerResultDto> answers) {
}