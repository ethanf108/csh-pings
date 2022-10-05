package edu.rit.csh.pings.repos;

import edu.rit.csh.pings.entities.WebNotification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebNotificationRepo extends CrudRepository<WebNotification, Long> {

    List<WebNotification> findAllByUsernameOrderByDate(String username, Pageable p);

    List<WebNotification> findAllByUsernameAndUnread(String username, boolean unread);
}
