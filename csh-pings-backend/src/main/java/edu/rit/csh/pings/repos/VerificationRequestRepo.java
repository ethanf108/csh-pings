package edu.rit.csh.pings.repos;

import edu.rit.csh.pings.entities.VerificationRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationRequestRepo extends CrudRepository<VerificationRequest, Long> {

    VerificationRequest findByToken(String token);
}
