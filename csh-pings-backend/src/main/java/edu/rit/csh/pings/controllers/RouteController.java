package edu.rit.csh.pings.controllers;

import edu.rit.csh.pings.auth.CSHUser;
import edu.rit.csh.pings.auth.UserAccessException;
import edu.rit.csh.pings.entities.Application;
import edu.rit.csh.pings.entities.Route;
import edu.rit.csh.pings.exchange.route.RouteCreate;
import edu.rit.csh.pings.exchange.route.RouteInfo;
import edu.rit.csh.pings.managers.ApplicationManager;
import edu.rit.csh.pings.managers.RouteManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class RouteController {

    private final ApplicationManager applicationManager;
    private final RouteManager routeManager;

    private final Log log = LogFactory.getLog("pings.route_controller");

    @PostMapping("/api/application/{uuid}/route")
    private void createRoute(@AuthenticationPrincipal CSHUser user, @PathVariable UUID uuid, @RequestBody RouteCreate create) {
        this.log.info("POST /api/application/" + uuid + "/route");
        final Application app = this.applicationManager.findByUUID(uuid).orElseThrow();
        if (!app.isMaintainer(user)) {
            throw new UserAccessException("Must be Maintainer");
        }
        if (app.isPublished()) {
            throw new IllegalArgumentException("Cannot edit published Application");
        }
        Route route = new Route();
        BeanUtils.copyProperties(create, route);
        route.setUuid(UUID.randomUUID());
        app.getRoutes().add(route);
        route.setApplication(app);
        route.setUserRegistrations(Set.of());
        route.setWebNotifications(Set.of());
        this.applicationManager.save(app);
        this.log.info("Created Route " + route.getUuid() + " for App " + app.getUuid());
    }

    @GetMapping("/api/application/{uuid}/route")
    public List<RouteInfo> getRoutes(@PathVariable UUID uuid) {
        this.log.info("GET /api/application/" + uuid + "/route");
        final Application app = this.applicationManager.findByUUID(uuid).orElseThrow();
        return app.getRoutes().stream().map(n -> {
            RouteInfo ret = new RouteInfo();
            BeanUtils.copyProperties(n, ret);
            return ret;
        }).toList();
    }

    @DeleteMapping("/api/application/{appUuid}/route/{routeUUID}")
    private void deleteRoute(@AuthenticationPrincipal CSHUser user, @PathVariable UUID appUuid, @PathVariable UUID routeUUID) {
        this.log.info("DELETE /api/application/" + appUuid + "/route/" + routeUUID);
        final Application app = this.applicationManager.findByUUID(appUuid).orElseThrow();
        if (!app.isMaintainer(user)) {
            throw new UserAccessException("Must be Maintainer");
        }
        if (app.isPublished()) {
            throw new IllegalArgumentException("Cannot edit published Application");
        }
        final Route route = this.routeManager.findByUUID(routeUUID).orElseThrow();
        app.getRoutes().remove(route);
        this.applicationManager.save(app);
        this.log.info("Deleted Route " + routeUUID + " from App " + appUuid);
    }
}
