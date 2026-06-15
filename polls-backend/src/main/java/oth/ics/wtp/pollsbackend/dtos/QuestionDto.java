package oth.ics.wtp.pollsbackend.dtos;

import oth.ics.wtp.pollsbackend.entities.QuestionType;

public record QuestionDto(long id, String text, QuestionType type) {
}