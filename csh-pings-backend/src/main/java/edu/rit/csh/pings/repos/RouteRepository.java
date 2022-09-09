package edu.rit.csh.pings.repos;

import edu.rit.csh.pings.entities.Route;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RouteRepository extends CrudRepository<Route, Long> {

    Route findByUuid(UUID uuid);
}
