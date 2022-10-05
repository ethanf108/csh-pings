package edu.rit.csh.pings.repos;

import edu.rit.csh.pings.entities.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApplicationRepo extends CrudRepository<Application, Long> {

    Page<Application> findAllByOrderByName(Pageable p);

    Application findByUuid(UUID uuid);

}
