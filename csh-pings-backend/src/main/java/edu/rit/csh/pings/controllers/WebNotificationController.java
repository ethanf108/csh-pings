package edu.rit.csh.pings.controllers;

import edu.rit.csh.pings.auth.CSHUser;
import edu.rit.csh.pings.exchange.webnotification.WebNotificationInfo;
import edu.rit.csh.pings.managers.WebNotificationManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class WebNotificationController {

    private final WebNotificationManager webNotificationManager;
    private final Log log = LogFactory.getLog("pings.web_notification_controller");

    @GetMapping("/api/web-notification")
    private List<WebNotificationInfo> getWebNotifications(
            @AuthenticationPrincipal CSHUser user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int length) {
        this.log.info("GET /api/web-notification");
        if (page < 0 || length < 0 || length > 100) {
            throw new IllegalArgumentException("Invalid parameters");
        }
        return this.webNotificationManager
                .findMostRecent(user.getUsername(), page, length)
                .stream()
                .map(wn -> {
                    WebNotificationInfo ret = new WebNotificationInfo();
                    BeanUtils.copyProperties(wn, ret);
                    ret.setApplicationUUID(wn.getRoute().getApplication().getUuid());
                    ret.setRouteUUID(wn.getRoute().getUuid());
                    return ret;
                }).toList();
    }

    @PostMapping("/api/web-notification/read")
    private void setRead(@AuthenticationPrincipal CSHUser user) {
        this.log.info("POST /api/web-notification/read");
        this.webNotificationManager.setRead(user.getUsername());
    }
}
