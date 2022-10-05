package edu.rit.csh.pings.managers;

import edu.rit.csh.pings.entities.WebNotification;
import edu.rit.csh.pings.repos.WebNotificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WebNotificationManager {

    private final WebNotificationRepo webNotificationRepo;

    public void save(WebNotification wn) {
        this.webNotificationRepo.save(wn);
    }

    public List<WebNotification> findMostRecent(String username, int page, int size) {
        return this.webNotificationRepo.findAllByUsernameOrderByDate(username, PageRequest.of(page, size));
    }

    public void setRead(String username) {
        for (WebNotification wn : this.webNotificationRepo.findAllByUsernameAndUnread(username, true)) {
            wn.setUnread(false);
            this.webNotificationRepo.save(wn);
        }
    }
}
