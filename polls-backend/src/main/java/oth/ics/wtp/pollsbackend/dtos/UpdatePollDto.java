package oth.ics.wtp.pollsbackend.dtos;

import java.time.LocalDate;

public record UpdatePollDto(
        String title,
        String description,
        LocalDate dueDate) {
}