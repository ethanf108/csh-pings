package edu.rit.csh.pings.repos;

import edu.rit.csh.pings.entities.ServiceConfiguration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceConfigurationRepo extends CrudRepository<ServiceConfiguration, Long> {

    <T extends ServiceConfiguration> T findByUuid(UUID uuid);

    <T extends ServiceConfiguration> List<T> findAllByUsernameIgnoreCase(String username);

}
