package oth.ics.wtp.pollsbackend.dtos;

import oth.ics.wtp.pollsbackend.entities.QuestionType;

public record AnswerResultDto(
        long questionId,
        String questionText,
        QuestionType type,
        String textValue,
        Boolean booleanValue,
        Integer numericValue) {
}