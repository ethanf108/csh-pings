package edu.rit.csh.pings.repos;

import edu.rit.csh.pings.entities.Route;
import edu.rit.csh.pings.entities.UserRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRegistrationRepo extends CrudRepository<UserRegistration, Long> {

    UserRegistration findByUuid(UUID uuid);

    List<UserRegistration> findAllByRouteAndUsername(Route route, String username);

    Page<UserRegistration> findAllByUsername(String username, Pageable p);
}
