package oth.ics.wtp.pollsbackend.dtos;

import oth.ics.wtp.pollsbackend.entities.QuestionType;

public record AggregateResultDto(
        long questionId,
        String questionText,
        QuestionType type,
        Double average,
        Integer yesCount,
        Integer noCount,
        Double averageLength) {
}