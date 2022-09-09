package edu.rit.csh.pings.repos;

import edu.rit.csh.pings.entities.ExternalToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalTokenRepo extends CrudRepository<ExternalToken, Long> {

    ExternalToken findByToken(String token);
}
