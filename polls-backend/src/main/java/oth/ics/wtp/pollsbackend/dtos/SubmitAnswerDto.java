package oth.ics.wtp.pollsbackend.dtos;

import oth.ics.wtp.pollsbackend.entities.QuestionType;

public record SubmitAnswerDto(
        long questionId,
        String textValue,
        Boolean booleanValue,
        Integer numericValue) {
}