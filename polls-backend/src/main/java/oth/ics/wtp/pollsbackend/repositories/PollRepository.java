package oth.ics.wtp.pollsbackend.repositories;

import oth.ics.wtp.pollsbackend.entities.Poll;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PollRepository extends CrudRepository<Poll, Long> {

    List<Poll> findByCreatorUsername(String username);
    Optional<Poll> findByTitle(String title);
    boolean existsByTitle(String title);
    long countByCreatorUsername(String username);
}