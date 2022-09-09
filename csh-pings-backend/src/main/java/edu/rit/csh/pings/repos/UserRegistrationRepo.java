package edu.rit.csh.pings.repos;

import edu.rit.csh.pings.entities.Route;
import edu.rit.csh.pings.entities.UserRegistration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRegistrationRepo extends CrudRepository<UserRegistration, Long> {

    List<UserRegistration> findAllByRouteAndUsername(Route route, String username);
}
