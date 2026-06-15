package oth.ics.wtp.pollsbackend.repositories;

import oth.ics.wtp.pollsbackend.entities.Question;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface QuestionRepository extends CrudRepository<Question, Long> {

    List<Question> findByPollId(long pollId);
    void deleteByPollId(long pollId);
}