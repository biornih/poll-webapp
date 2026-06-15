package oth.ics.wtp.pollsbackend.dtos;

import oth.ics.wtp.pollsbackend.entities.QuestionType;

public record CreateQuestionDto(String text, QuestionType type) {
}