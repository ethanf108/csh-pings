package edu.rit.csh.pings.external;

import edu.rit.csh.pings.entities.Route;
import edu.rit.csh.pings.entities.WebNotification;
import edu.rit.csh.pings.entities.WebNotificationConfiguration;
import edu.rit.csh.pings.managers.WebNotificationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WebNotificationService implements ExternalService<WebNotificationConfiguration> {

    private final WebNotificationManager webNotificationManager;

    @Override
    public void sendPing(Route route, WebNotificationConfiguration config, String body) {
        WebNotification wn = new WebNotification();
        wn.setUsername(config.getUsername());
        wn.setBody(body);
        wn.setUnread(true);
        wn.setRoute(route);
        wn.setDate(LocalDateTime.now());
        wn.setUuid(UUID.randomUUID());
        this.webNotificationManager.save(wn);
    }

    @Override
    public void sendVerification(WebNotificationConfiguration config) {
        //Do nothing, web notifications are already verified
    }
}
