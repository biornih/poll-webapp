package oth.ics.wtp.pollsbackend.repositories;

import oth.ics.wtp.pollsbackend.entities.Invitation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends CrudRepository<Invitation, Long> {

    List<Invitation> findByInviteeUsername(String username);
    List<Invitation> findByPollId(long pollId);
    Optional<Invitation> findByPollIdAndInviteeUsername(long pollId, String username);
    boolean existsByPollIdAndInviteeUsername(long pollId, String username);
    int countByPollId(long pollId);
    void deleteByPollIdAndInviteeUsername(long pollId, String username);
}