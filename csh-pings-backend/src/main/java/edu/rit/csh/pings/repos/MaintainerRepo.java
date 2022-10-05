package edu.rit.csh.pings.repos;

import edu.rit.csh.pings.entities.Maintainer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintainerRepo extends CrudRepository<Maintainer, Long> {

}
