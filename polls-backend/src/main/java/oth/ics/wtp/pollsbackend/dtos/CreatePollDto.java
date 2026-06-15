package oth.ics.wtp.pollsbackend.dtos;

import java.time.LocalDate;
import java.util.List;

public record CreatePollDto(
        String title,
        String description,
        LocalDate dueDate,
        List<CreateQuestionDto> questions) {
}