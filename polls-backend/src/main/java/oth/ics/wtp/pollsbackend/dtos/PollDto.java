package oth.ics.wtp.pollsbackend.dtos;

import java.time.LocalDate;
import java.util.List;

public record PollDto(
        long id,
        String title,
        String description,
        LocalDate dueDate,
        boolean finished,
        AppUserDto creator,
        List<QuestionDto> questions) {
}