package oth.ics.wtp.pollsbackend.repositories;

import oth.ics.wtp.pollsbackend.entities.Answer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AnswerRepository extends CrudRepository<Answer, Long> {

    List<Answer> findByInvitationId(long invitationId);
    List<Answer> findByQuestionId(long questionId);
}