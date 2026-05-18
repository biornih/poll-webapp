package oth.ics.wtp.pollsbackend.repositories;

import oth.ics.wtp.pollsbackend.entities.AppUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AppUserRepository extends CrudRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);

}
