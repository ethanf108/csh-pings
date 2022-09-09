package edu.rit.csh.pings.controllers;

import edu.rit.csh.pings.auth.CSHUser;
import edu.rit.csh.pings.exchange.webnotification.WebNotificationInfo;
import edu.rit.csh.pings.managers.WebNotificationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class WebNotificationController {

    private WebNotificationManager webNotificationManager;

    @GetMapping("/api/web-notification")
    private List<WebNotificationInfo> getWebNotifications(
            @AuthenticationPrincipal CSHUser user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int length) {
        if (page < 0 || length < 0 || length > 100) {
            throw new IllegalArgumentException("Invalid parameters");
        }
        return this.webNotificationManager
                .findMostRecent(user.getUsername(), page, length)
                .stream()
                .map(wn -> {
                    WebNotificationInfo ret = new WebNotificationInfo();
                    BeanUtils.copyProperties(wn, ret);
                    ret.setApplicationUUID(wn.getRoute().getUuid());
                    return ret;
                }).toList();
    }
}
